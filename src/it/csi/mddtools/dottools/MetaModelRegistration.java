
package it.csi.mddtools.dottools;

import java.io.IOException;
import java.net.URL;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
//import org.openarchitectureware.workflow.util.ResourceLoader;
//import org.openarchitectureware.workflow.util.ResourceLoaderImpl;
//import org.openarchitectureware.workflow.util.ResourceLoaderFactory;
//import org.openarchitectureware.xtext.XtextFile;
import org.eclipse.emf.mwe.core.resources.ResourceLoader;
import org.eclipse.emf.mwe.core.resources.ResourceLoaderFactory;
import org.eclipse.emf.mwe.core.resources.ResourceLoaderImpl;
import org.eclipse.xtext.XtextPackage;


public class MetaModelRegistration {
	
    static {
    	org.eclipse.zest.internal.dot.parser.DotStandaloneSetup.doSetup();
		
	}
	
	
}
