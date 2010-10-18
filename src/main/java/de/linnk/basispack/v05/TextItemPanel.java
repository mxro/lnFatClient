package de.linnk.basispack.v05;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.dts.spell.swing.JTextComponentSpellChecker;

import de.linnk.domain.Item;
import de.linnk.domain.TextItem;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.fatclient.document.ItemPanel;
import de.linnk.fatclient.icons.Icons;
import de.linnk.gwt.LinnkGWTUtils;
import de.linnk.nx.swing.SwingRenderer;
import de.linnk.streaming.DocumentLoader;
import de.mxro.shef.MxroEditorPane;
import de.mxro.swing.JMyPanel;

/**
*
* @author mx
*
*
* @scr.component
* @scr.service interface="de.linnk.nx.swing.SwingRenderer"
*
*/
public class TextItemPanel extends ItemPanel<TextItem> implements de.linnk.nx.swing.SwingRenderer<TextItem>{

	public static final long serialVersionUID = 1L;
	
	protected JMyPanel jHolder = null;
	
	private boolean spellCheckEnabled;
	
	boolean hyperlinkEntered=false;

	protected EditorPane jTextField = null;
	
	@Override
	public JComponent render(TextItem o) {
		TextItemPanel tp =  new TextItemPanel();
		tp.setItem(o);
		return tp;
	}
	
	public class EditorPane extends MxroEditorPane {

		public transient JTextComponentSpellChecker textSpellChecker;
		
		private static final long serialVersionUID = 1L;
		
		private final Component container;
		
		public EditorPane(Component container) {
			super();
			this.container = container;
			this.setFont(LinnkFatClient.textFont);
		}

		@Override
		public void adjustSize() {
			TextItemPanel.this.adjustSize();
		}
		
		@Override
		public Dimension getPreferredSize() {
			super.setPreferredSize(null);
		
			this.setMaximumSize(new Dimension(this.container.getWidth(), Short.MAX_VALUE));
			this.setMinimumSize(new Dimension(this.container.getWidth(), 0));
			this.setSize(new Dimension(this.container.getWidth(), Short.MAX_VALUE));
			final Dimension dim = super.getPreferredSize();
			//((HTMLEditorKit) this.getEditorKit()).
			return new Dimension(this.container.getWidth(), dim.height); 
		}
	}
	
	@Override
	public void save() {
		
	}

	@Override
	public Icon getIcon() {
		return Icons.getSmallIcon("text.png");
		
	}

	public static boolean oneDo=false;
	
	@Override
	protected void afterDeselect() {
		super.afterDeselect();
		this.getJTextField().setEditable(false);
	}


	@Override
	protected void afterSelect() {
		super.afterSelect();
		if (! oneDo) {
			oneDo = true;
		
		(new Runnable() {

			public void run() {
				try {
					Thread.sleep(4);
				} catch (final InterruptedException e) {
					// shit happens ...
				}
				TextItemPanel.this.getJTextField().requestFocusInWindow();
				
				oneDo = false;
			}
				
			}).run();
		}
		
		if (!this.hyperlinkEntered) {
			TextItemPanel.this.getJTextField().setEditable(true);
		}
		this.hyperlinkEntered = false;
		
	}


	@Override
	public Item generateChangedItem() {
		// get Item in document
		
		if ((this.getItem()).getTextData().equals( this.getJTextField().getText()))
			return null;
		final TextItem changedItem = new TextItem(this.getItem().getCreator(), this.getItem().getId(), this.getItem().getDocument(), this.getJTextField().getText());
		return changedItem;
			
	}



	@Override
	public void updatePanel() {
		if (this.jTextField == null) {
			this.initialize();
			return; // this is a little, dirty trick ...
		}
	
		this.getJTextField().setText(this.getItem().getTextData());
		
		
		this.adjustSize();
	}

	
	
	
	public void adjustSize() {
		if (this.getItem() == null)
			return;
		final boolean oldEditable = this.getJTextField().isEditable();
		this.getJTextField().setEditable(true);
		// believe me: this nasty piece of code took me hours before it worked
		 // the way i want it!
		 //
		
		final int width = this.getJHolder().getSize().width;
		
		final int height = this.getJTextField().getPreferredSize().height;
		
		final Dimension size  =  new Dimension(width, height);
		
		
		this.getJHolder().setSize(size);
		this.getJTextField().setSize(this.jHolder.getSize());
		Dimension thisSize = new Dimension(Short.MAX_VALUE, size.height);
		this.setMaximumSize(thisSize);
		this.setMinimumSize(thisSize);
		
		thisSize = new Dimension(0, size.height);
		this.setPreferredSize(thisSize);
		this.getJTextField().setEditable(oldEditable);
	}
	
	
	
	/**
	 * This method initializes 
	 * 
	 */

	public TextItemPanel() {
		super();
	
	}


	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		
		this.add(this.getJHolder(), null);
		
		this.getJHolder().setAlignmentX(Component.LEFT_ALIGNMENT);
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.getJTextField().setAlignmentX(Component.LEFT_ALIGNMENT);
		
		//jEditorPane.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		this.addComponentListener(new java.awt.event.ComponentAdapter() {
			@Override
			public void componentResized(java.awt.event.ComponentEvent e) {
				 TextItemPanel.this.adjustSize();
			}
		});
		
		de.mxro.utils.Utils.addKeystrokesToBeanTree(
				this.getItemspanel().getDocumentPanel().getHolder().getWindowInterface().getGlobalActions(),
				this);
	}
	
	
	
	
	
	public EditorPane getJTextField() {
		if (this.jTextField == null) {
			
			this.jTextField = new EditorPane(this.getJHolder());
			
			this.jTextField.addFocusListener(this.getFocusListener());
			this.jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
				@Override
				public void focusLost(java.awt.event.FocusEvent e) {
					//if (TextItemPanel.this.jTextField.textSpellChecker != null) {
						SwingUtilities.invokeLater(new Runnable() {

							public void run() {
								LinnkFatClient.application.stopRealtimeSpellCheck(TextItemPanel.this.jTextField);
								TextItemPanel.this.jTextField.textSpellChecker = null;
								
								//TextItemPanel.this.jTextField.textSpellChecker = null;
							}
							
						});
						
					//}
					// save changes
					TextItemPanel.this.updateItem();

					TextItemPanel.this.adjustSize();
				}
			});
			
			this.jTextField.addKeyListener(new java.awt.event.KeyAdapter() {
				@Override
				public void keyTyped(java.awt.event.KeyEvent e) {					
					TextItemPanel.this.adjustSize();
					
					if (TextItemPanel.this.spellCheckEnabled == false) {
						LinnkFatClient.application.startRealtimeSpellCheck(TextItemPanel.this.jTextField, TextItemPanel.this.jTextField.getLocale());
						TextItemPanel.this.spellCheckEnabled = true;
					}
				}
			}); 
			
			
			
			this.jTextField.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent arg0) {
					//System.out.println(""+arg0.getClickCount());
					if (arg0.getClickCount() == 2) {
						if (LinnkGWTUtils.getProxies(LinnkProxy.class, TextItemPanel.this.getItem().getDocument().getRootItem(TextItemPanel.this.getItem())).size() > 0) {
							LinnkProxy linnkproxy = LinnkGWTUtils.getProxies(LinnkProxy.class,  TextItemPanel.this.getItem().getDocument().getRootItem(TextItemPanel.this.getItem())).firstElement();
							
							NodeDocument doc = DocumentLoader.singelton.loadForDocument(TextItemPanel.this.getDocument(), linnkproxy.getLink().toURI());
							TextItemPanel.this.getItemspanel().getDocumentPanel().getHolder().showDocument(
									
							doc);
						}
						return;
					}
					//TextItemPanel.this.getItemspanel().deselectAll(TextItemPanel.this.getItem());
				}

				public void mouseEntered(MouseEvent arg0) {
					
				}

				public void mouseExited(MouseEvent arg0) {
				
				}

				public void mousePressed(MouseEvent arg0) {
					TextItemPanel.this.getItemspanel().deselectAll(TextItemPanel.this.getItem().getCompleteID());
					
				}

				public void mouseReleased(MouseEvent arg0) {
					
				}
				
			});
			
			
		}
		
		
		return this.jTextField;
	}


	public JMyPanel getJHolder() {
		if (this.jHolder == null) {
			this.jHolder = new JMyPanel(null);
			this.jHolder.setSize(0,0);
			this.jHolder.setOpaque(false);
			this.jHolder.add(this.getJTextField());
			
			this.getJTextField().setSize(new Dimension(800, 30)); // i really dont know why this helps ...
			
						
			this.getJTextField().setEditable(false);	
		}
		return this.jHolder;
	}



}  //  @jve:decl-index=0:visual-constraint="73,50"
