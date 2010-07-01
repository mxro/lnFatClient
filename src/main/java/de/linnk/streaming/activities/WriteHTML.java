package de.linnk.streaming.activities;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import de.linnk.domain.Document;
import de.linnk.streaming.DocumentLoader;
import de.linnk.streaming.views.LoadOnDemandDocument;
import de.linnk.streaming.views.View;
import de.linnk.transform.ChangeOutgoingLinks;
import de.linnk.transform.Transformation;
import de.mxro.filesystem.File;
import de.mxro.filesystem.FileSystemObject;
import de.mxro.filesystem.Folder;
import de.mxro.string.filter.Filter;
import de.mxro.utils.URI;
import de.mxro.utils.Utils;
import de.mxro.utils.background.Activity;
import de.mxro.utils.log.UserError;

public class WriteHTML implements Activity {
	
	private final LoadOnDemandDocument newDocument;
	private final View plainXMLView;
	private final File htmlFile;
	private final boolean xmlDeclaration;
	
	public static void applyFilterOnFile(File file, Filter filter) throws IOException {
		String s = Utils.fromInputStream(file.getInputStream());
		String altered = filter.perform(s);
		file.getOutputStream().write(altered.getBytes());
	}
	
	public void run() {
		final Document htmlDocument = DocumentLoader.singelton.loadFromFile(newDocument.getFile());
		new ChangeOutgoingLinks(".html", Transformation.NONE).transform(htmlDocument);
		final Folder temporaryFolder = FileSystemObject.newIncludedRootFolder("");
		final File temporaryDocumentWithChangedLinks = this.plainXMLView.writeView(htmlDocument, temporaryFolder).getFile();
		
		if (temporaryDocumentWithChangedLinks == null) {
			UserError.singelton.log("Could not write HTML. View could not be written.", UserError.Priority.HIGH);
			return;
		}
			/* html */
			try {
				
				final Source xmlSource =  new StreamSource(temporaryDocumentWithChangedLinks.getInputStream());
				
				final URI xslURI = newDocument.getFile().getURI().changeExtension(de.linnk.domain.LinnkConstants.xslExtension);
				final File xslFile = newDocument.getFile().getOwner().getFile(xslURI);
				final Source xsltSource = new StreamSource(xslFile.getInputStream());
				final TransformerFactory transFact = TransformerFactory.newInstance();
				final Transformer trans = transFact.newTransformer(xsltSource);
				
				final OutputStream os = this.htmlFile.getOutputStream();
				if (os == null) {
					UserError.singelton.log("WriteHTML: could not find file: "+this.htmlFile.getURI(), UserError.Priority.HIGH);
					return;
				}
				
				trans.transform(xmlSource, new StreamResult(this.htmlFile.getOutputStream()));
				if (!xmlDeclaration) {
					applyFilterOnFile(this.htmlFile, Filter.regExReplace("[<][?]xml[^>]*>", "", Filter.identity));
				}
			} catch (final IOException e) {
				UserError.singelton.log(e);
			} catch (final TransformerConfigurationException e) {
				UserError.singelton.log(e);
			} catch (final TransformerFactoryConfigurationError e) {
				UserError.singelton.log(e);
			} catch (final TransformerException e) {
				UserError.singelton.log(e);
			}
		
	}

	public WriteHTML(final LoadOnDemandDocument newDocumentFile, final View view, final File file, final boolean xmlDeclaration) {
		super();
		if (file == null)
			throw new IllegalArgumentException("Please Specify file!");
		this.newDocument = newDocumentFile;
		this.plainXMLView = view;
		this.htmlFile = file;
		this.xmlDeclaration = xmlDeclaration;
	}

	
}
