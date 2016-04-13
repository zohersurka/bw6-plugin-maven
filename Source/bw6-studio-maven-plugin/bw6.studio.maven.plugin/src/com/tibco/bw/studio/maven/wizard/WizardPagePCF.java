package com.tibco.bw.studio.maven.wizard;

import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.tibco.bw.studio.maven.helpers.ManifestParser;
import com.tibco.bw.studio.maven.helpers.ModuleHelper;
import com.tibco.bw.studio.maven.modules.BWModule;
import com.tibco.bw.studio.maven.modules.BWModuleType;
import com.tibco.bw.studio.maven.modules.BWPCFModule;
import com.tibco.bw.studio.maven.modules.BWProject;

public class WizardPagePCF extends WizardPage 
{
	private Composite container;
	private BWProject project;
	private String bwEdition;

	private Text appPCFTarget;
	private Text appPCFCred;
	private Text appPCFOrg;
	private Text appPCFSpace;
	private Text appPCFAppName;
	private Text appPCFInstances;
	private Text appPCFMemory;
	private Text appPCFBuildpack;
	


	protected WizardPagePCF ( String pageName , BWProject project ) 
	{
		super(pageName);
		this.project = project;		 
		setTitle("Maven Configuration Details for Plugin Code for Apache Maven and TIBCO BusinessWorks™");
		setDescription("Enter the GroupId and ArtifactId for for Maven POM File generation. \nThe POM files will be generated for Projects listed below and a Parent POM file will be generated aggregating the Projects");	
	}

	@Override
	public void createControl(Composite parent) 
	{
		container = new Composite(parent, SWT.NONE);

		
		  bwEdition = "bw6";
		  try
		  {
			  Map<String,String> manifest = ManifestParser.parseManifest(project.getModules().get(0).getProject());
			  if(manifest.containsKey("TIBCO-BW-Edition") && manifest.get("TIBCO-BW-Edition").equals("bwcf"))
			  {
				  bwEdition="bwcf";
			  }
			  else
			  {
				  bwEdition="bw6";
			  }
		  } catch (Exception e) 
		  {
			  e.printStackTrace();
		  }
		
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 4;
		setApplicationPCFPOMFields();
		
		setControl(container);
		setPageComplete(true);


	}
	
	private void setApplicationPCFPOMFields() 
	{
		
		
		Label targetLabel = new Label(container, SWT.NONE);
		targetLabel.setText("PCF Target");

		appPCFTarget = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFTarget.setText( "https://api.run.pivotal.io");
		GridData targetData = new GridData(150, 15);
		appPCFTarget.setLayoutData(targetData);
		
		Label credLabel = new Label(container, SWT.RIGHT);
		credLabel.setText( "PCF Server Name" );

		appPCFCred = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFCred.setText("PCF_UK_credential");
		GridData credData = new GridData(100, 15);
		appPCFCred.setLayoutData(credData);
		
		Label orgLabel = new Label(container, SWT.RIGHT);
		orgLabel.setText( "PCF Org" );

		appPCFOrg = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFOrg.setText("tibco");
		GridData orgData = new GridData(100, 15);
		appPCFOrg.setLayoutData(orgData);
		
		
		Label spaceLabel = new Label(container, SWT.RIGHT);
		spaceLabel.setText( "PCF Space" );
		
		appPCFSpace = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFSpace.setText("development");
		GridData spaceData = new GridData(100, 15);
		appPCFSpace.setLayoutData(spaceData);
		
		Label appNameLabel = new Label(container, SWT.RIGHT);
		appNameLabel.setText( "App Name" );
		
		appPCFAppName = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFAppName.setText("AppName");
		GridData appNameData = new GridData(100, 15);
		appPCFAppName.setLayoutData(appNameData);
		
		
		Label instancesLabel = new Label(container, SWT.RIGHT);
		instancesLabel.setText( "App Instances" );
		
		appPCFInstances = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFInstances.setText("1");
		GridData instancesData = new GridData(50, 15);
		appPCFInstances.setLayoutData(instancesData);
		
		
		Label memoryLabel = new Label(container, SWT.RIGHT);
		memoryLabel.setText( "App Memory" );
		
		appPCFMemory = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFMemory.setText("1024");
		GridData memoryData = new GridData(50, 15);
		appPCFMemory.setLayoutData(memoryData);
		
		
		Label buildpackLabel = new Label(container, SWT.RIGHT);
		buildpackLabel.setText( "App Buildpack" );
		
		appPCFBuildpack = new Text(container, SWT.BORDER | SWT.SINGLE);
		appPCFBuildpack.setText("bw-buildpack");
		GridData buildpackData = new GridData(200, 15);
		appPCFBuildpack.setLayoutData(buildpackData);
		
		Label pcfServicesLabel = new Label(container, SWT.RIGHT);
		pcfServicesLabel.setText( "PCF Services" );
		
		/////// Add a Button to select PCF services //////
		final Button servicesButton = new Button(container, SWT.PUSH | SWT.BORDER);
		servicesButton.setText("Select Services");
		servicesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			  ////set BWCFModule with above provided values//////
				for( BWModule module : project.getModules() )
				{
					if( module.getType() == BWModuleType.Application )
					{
						module.setBwpcfModule(setBWPCFValues(module));
						break;
					}
				}
				
				//call Services Wizard
			  PCFServiceWizard serviceWizard =	new PCFServiceWizard(project);	
			  WizardDialog dialog = new WizardDialog(container.getShell(),serviceWizard);
			  if (dialog.open() == Window.OK) 
			  {
					project = serviceWizard.getProject();
			  }
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
		    }
		});
		

	}
	
	
	private BWPCFModule setBWPCFValues(BWModule module){
		
		////BWPCFModule will not be null if its set through Services Wizard ///////
		
		BWPCFModule bwpcf=module.getBwpcfModule();
		if(bwpcf==null){
			bwpcf=new BWPCFModule();
		}
		
		bwpcf.setTarget(appPCFTarget.getText());
		bwpcf.setCredString(appPCFCred.getText());
		bwpcf.setOrg(appPCFOrg.getText());
		bwpcf.setSpace(appPCFSpace.getText());
		bwpcf.setAppName(appPCFAppName.getText());
		bwpcf.setInstances(appPCFInstances.getText());
		bwpcf.setMemory(appPCFMemory.getText());
		bwpcf.setBuildpack(appPCFBuildpack.getText());
		
		return bwpcf;
		
	}

	public BWProject getUpdatedProject() 
	{
		for (BWModule module : project.getModules() )
		{
			
			if(bwEdition.equals("bwcf") && module.getType() == BWModuleType.Application){
				module.setBwpcfModule(setBWPCFValues(module));
			}
			
			
			//module.setOverridePOM( buttonMap.containsKey( module.getArtifactId() ) ? (buttonMap.get(module.getArtifactId())).getSelection() : true  );
		}
		
		
		return project;
	}


}
