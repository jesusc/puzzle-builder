package ml2.load;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.emf.emfatic.core.EmfaticResource;
import org.eclipse.ocl.pivot.Model;
import org.eclipse.ocl.pivot.resource.CSResource;
import org.eclipse.ocl.xtext.completeocl.CompleteOCLStandaloneSetup;
import org.eclipse.ocl.xtext.completeocl.utilities.CompleteOCLCSResource;
import org.eclipse.ocl.xtext.completeoclcs.CompleteOCLDocumentCS;

public class ModelLoader {

    
    public static Resource read(String text) throws IOException {
            EmfaticResource resource = new EmfaticResource(URI.createURI("in-memory"));
            resource.load(new ByteArrayInputStream(text.getBytes()), null);
            return resource;
    }

    public static Model readOCL(Resource mm, String ocl) throws IOException {
    	return new OclLoader(mm).read(ocl);
    }
    
	private static class OclLoader {

		private Resource metamodel;

		public OclLoader(Resource metamodel) {
			this.metamodel = new XMIResourceImpl();
			this.metamodel.getContents().addAll(metamodel.getContents());
		}

		protected Model read(String oclText) throws IOException {			
			File metaFile = new File("/tmp/web.ecore");
			metamodel.save(new FileOutputStream(metaFile), null);
			
			File tempFile = new File("/tmp/web.ocl");
			oclText = "import '" + metaFile.getAbsolutePath() + "'" + oclText;
			IOUtils.write(oclText, new FileOutputStream(tempFile), Charset.defaultCharset());
			
			//ASResource as = oclToAs(URI.createFileURI(path));
			CompleteOCLDocumentCS doc = loadCompleteDoc(URI.createFileURI(tempFile.getAbsolutePath()));
			CompleteOCLCSResource r = (CompleteOCLCSResource) doc.eResource();
			if (!r.getErrors().isEmpty()) {
				for (Diagnostic diagnostic : r.getErrors()) {
					System.out.println("Diagnostic: " + diagnostic);
				}
				System.out.println("Error!! NOTIFY");
			}
			
			CompleteOCLDocumentCS document = (CompleteOCLDocumentCS) r.getContents().get(0);
			Model pivot = (Model) document.getPivot();
			
			return pivot;
		}

		protected CompleteOCLDocumentCS loadCompleteDoc(URI uri) {
			CompleteOCLStandaloneSetup.doSetup();
			
			ResourceSet rs = new ResourceSetImpl();
			CSResource r = (CSResource) rs.getResource(uri, true);
			return (CompleteOCLDocumentCS) r.getContents().get(0);
		}		
	}
}
