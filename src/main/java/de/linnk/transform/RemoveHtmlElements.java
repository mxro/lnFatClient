package de.linnk.transform;

import de.linnk.domain.Document;
import de.linnk.domain.Item;
import de.linnk.domain.ItemBuilder;
import de.linnk.domain.ItemChange;
import de.linnk.domain.TextItem;
import de.linnk.fatclient.application.LinnkFatClient;
import de.linnk.utils.Utils;

public class RemoveHtmlElements extends Transformation {
	
	private final String htmlElement;
	
	@Override
	public void applyTransformation(Document doc) {
		final ItemBuilder ib = new ItemBuilder(doc, LinnkFatClient.currentUser);
		for (final Item i : doc.getItems()) {
			final Item textItem = i.getItem(TextItem.class);
			if (textItem != null) {
				final String textO = ((TextItem) textItem).getTextData();
				String text = textO.replaceAll("<"+this.htmlElement+"[^>]*>", "");
				text = text.replaceAll("</"+this.htmlElement+"[^/]*>", "");
				
				if (!textO.equals(text)) {
					System.out.println("process text: "+textO);
					System.out.println("result: "+text);
					final Item newTextItem = ib.newTextItemFromTextData(text);
					final Item newItem = Utils.replace(doc, textItem, newTextItem);
					final ItemChange ic = ItemChange.newModifyItem(newItem, i, LinnkFatClient.currentUser);
					doc.doChange(ic);
				}
			}
		}
			
	}

	public RemoveHtmlElements(final String htmlElement, Transformation next) {
		super(next);
		this.htmlElement = htmlElement;
	}

	
	
}
