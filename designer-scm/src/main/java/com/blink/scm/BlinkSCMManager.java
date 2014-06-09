package com.blink.scm;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;
import org.apache.maven.cli.MavenCli;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.gitblit.client.GitblitClient;
import com.gitblit.client.GitblitRegistration;
import com.gitblit.models.RepositoryModel;

import org.apache.maven.model.*;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class BlinkSCMManager implements SCMManager {

	private static final String PATH_SEPARATOR = File.separator;
	
	private static final Logger logger = Logger.getLogger(BlinkSCMManager.class);

	static {

		System.setProperty("javax.net.ssl.keyStore","/Users/rpoosar/Documents/serverKeyStore.jks");
		System.setProperty("javax.net.ssl.trustStore", "/Users/rpoosar/Documents/serverTrustStore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "gitblit");
		System.setProperty("javax.net.ssl.trustStorePassword", "gitblit");
		System.setProperty("maven.home", "/Users/rpoosar/Documents/apache-maven-3.0.5");
	}


	private GitblitClient client;
	private String username; 
	private String password; 
	private String remoteRepoUrl; 
	private String localWorkspace;

	private String s; 

	public BlinkSCMManager() {

	}

	/* (non-Javadoc)
	 * @see com.blink.scm.SCMManager#init()
	 */
	public void init() throws IOException  {
		GitblitRegistration registration = new GitblitRegistration("test",remoteRepoUrl,username,password.toCharArray());
		client = new GitblitClient(registration);
        System.out.println("Login into " + remoteRepoUrl);
		client.login();
	}


	/* (non-Javadoc)
	 * @see com.blink.scm.SCMManager#createRemoteRepository(java.lang.String)
	 */
	public  void createRemoteRepository(String repositoryName) throws IOException, RepositoryExistsException {
		
		logger.debug("Starting to create Remote repository with name " +  repositoryName);
		System.err.println("Hello");
		RepositoryModel repositoryModel = new RepositoryModel();
		repositoryModel.name = repositoryName;
		repositoryModel.isBare = true;

		if ( client.getRepository(repositoryName) != null ) 
			throw new RepositoryExistsException("Remote Repository " + repositoryName+ " already exists");

		client.createRepository(repositoryModel,null);
		
		logger.debug("Created Remote repository with name " +  repositoryName);

	}

	/* (non-Javadoc)
	 * @see com.blink.scm.SCMManager#createLocalRepository(java.lang.String)
	 */
	public String createLocalRepository(String repositoryName) throws GitAPIException, RepositoryExistsException,IOException,XmlPullParserException {

		final String localRepo = localWorkspace + PATH_SEPARATOR + repositoryName;
        
		InitCommand init = Git.init();
		File initFile = new File(localRepo);
		System.out.println(localRepo);
		System.out.println(localWorkspace);
		System.out.println(repositoryName);
		if(initFile.exists()) 
			throw new RepositoryExistsException("Local Repository " + repositoryName+ "already exists in Workspace");
		initFile.mkdirs();
		init.setDirectory(initFile);

		init.call();
		
		createMavenProject(localRepo, repositoryName);
		if(!repositoryName.equals("test"))
		new File(localWorkspace + "/" + repositoryName + "/" + repositoryName+ "/src/main/java").mkdirs();

		
		return localRepo;

	}

	private void createMavenProject(String projectDirectory, String projectName)throws XmlPullParserException,IOException {
		MavenCli cli = new MavenCli();
		int result = cli.doMain(new String[]{"archetype:generate","-DgroupId=com.mycompany.app", "-DartifactId="+ projectName,"-DarchetypeGroupId=org.apache.maven.archetypes", 
				"-DarchetypeArtifactId=maven-archetype-webapp", "-DarchetypeVersion=1.0","-DinteractiveMode=false"},
				projectDirectory,  System.out, System.out);
		System.out.println("projectname:"+projectDirectory);
		String baseDir = projectDirectory + "/" + projectName;
		MavenXpp3Reader reader = new MavenXpp3Reader();
		Model model = reader.read(new FileInputStream(new File(baseDir, "/pom.xml")));
		Dependency d1=new Dependency();
		Dependency d2=new Dependency();
		Dependency d3=new Dependency();
		Dependency d4=new Dependency();
		Dependency d5=new Dependency();
		Dependency d6=new Dependency();
		Dependency d7=new Dependency();
		Dependency d8=new Dependency();
		Dependency d9=new Dependency();
		Dependency d10=new Dependency();
		Dependency d11=new Dependency();
		Dependency d12=new Dependency();
		Dependency d13=new Dependency();
		Dependency d14=new Dependency();
		Dependency d15=new Dependency();
		Dependency d16=new Dependency();
		Dependency d17=new Dependency();
		Dependency d18=new Dependency();
		Dependency d19=new Dependency();
		Dependency d20=new Dependency();
		Dependency d21=new Dependency();
		Dependency d22=new Dependency();
		Dependency d23=new Dependency();
		Dependency d24=new Dependency();
		Dependency d25=new Dependency();
		MavenXpp3Writer writer = new MavenXpp3Writer();
		
		d1.setGroupId("org.slf4j");
		d1.setArtifactId("slf4j-log4j12");
		d1.setVersion("1.7.6");
		model.addDependency(d1);
		
		d2.setGroupId("org.springframework");
		d2.setArtifactId("spring-beans");
		d2.setVersion("3.2.8.RELEASE");
		model.addDependency(d2);
		
		d3.setGroupId("net.sf.dozer");
		d3.setArtifactId("dozer");
		d3.setVersion("5.1");
		model.addDependency(d3);
		
		d4.setGroupId("c3p0");
		d4.setArtifactId("c3p0");
		d4.setVersion("0.8.4.5");
		model.addDependency(d4);
		
		d5.setGroupId("org.springframework");
		d5.setArtifactId("spring-context");
		d5.setVersion("3.2.8.RELEASE");
		model.addDependency(d5);
		
		d6.setGroupId("org.springframework");
		d6.setArtifactId("spring-orm");
		d6.setVersion("3.2.8.RELEASE");
		model.addDependency(d6);
		
		d7.setGroupId("javax.servlet");
		d7.setArtifactId("javax.servlet-api");
		d7.setVersion("3.0.1");
		d7.setScope("provided");
		model.addDependency(d7);
		
		d8.setGroupId("javax.persistence");
		d8.setArtifactId("persistence-api");
		d8.setVersion("1.0.2");
		model.addDependency(d8);
		
		d9.setGroupId("org.springframework");
		d9.setArtifactId("spring-web");
		d9.setVersion("3.2.8.RELEASE");
		model.addDependency(d9);
		
		d10.setGroupId("org.springframework");
		d10.setArtifactId("spring-tx");
		d10.setVersion("3.2.8.RELEASE");
		model.addDependency(d10);
		
		d11.setGroupId("org.apache.cxf");
		d11.setArtifactId("cxf-bundle");
		d11.setVersion("3.0.0-milestone1");
		model.addDependency(d11);
		
		d12.setGroupId("org.hibernate");
		d12.setArtifactId("hibernate-entitymanager");
		d12.setVersion("4.1.3.Final");
		model.addDependency(d12);
		
		d13.setGroupId("mysql");
		d13.setArtifactId("mysql-connector-java");
		d13.setVersion("5.1.28");
		model.addDependency(d13);
		
		d14.setGroupId("javax.ws.rs");
		d14.setArtifactId("jsr311-api");
		d14.setVersion("1.1.1");
		model.addDependency(d14);
		
		d15.setGroupId("org.apache.cxf");
		d15.setArtifactId("cxf-core");
		d15.setVersion("3.0.0-milestone1");
		model.addDependency(d15);
		
		d16.setGroupId("com.google.code.gson");
		d16.setArtifactId("gson");
		d16.setVersion("2.2.4");
		model.addDependency(d16);
		
		d17.setGroupId("org.apache.cxf");
		d17.setArtifactId("cxf-rt-transports-http");
		d17.setVersion("3.0.0-milestone1");
		model.addDependency(d17);
		
		d18.setGroupId("org.apache.cxf");
		d18.setArtifactId("cxf-rt-frontend-jaxws");
		d18.setVersion("3.0.0-milestone1");
		model.addDependency(d18);
		
		d19.setGroupId("org.springframework.javaconfig");
		d19.setArtifactId("spring-javaconfig");
		d19.setVersion("1.0.0.m3");
		model.addDependency(d19);
		
		d20.setGroupId("org.hibernate.javax.persistence");
		d20.setArtifactId("hibernate-jpa-2.0-api");
		d20.setVersion("1.0.1.Final");
		model.addDependency(d20);
		
		d21.setGroupId("javax.ws.rs");
		d21.setArtifactId("javax.ws.rs-api");
		d21.setVersion("2.0");
		model.addDependency(d21);
		
		d22.setGroupId("org.codehaus.jettison");
		d22.setArtifactId("jettison");
		d22.setVersion("1.3.3");
		model.addDependency(d22);
		
		d23.setGroupId("javax.validation");
		d23.setArtifactId("validation-api");
		d23.setVersion("1.0.0.GA");
		model.addDependency(d23);
		
		d24.setGroupId("org.hibernate");
		d24.setArtifactId("hibernate-validator");
		d24.setVersion("4.3.1.Final");
		model.addDependency(d24);
		
		d25.setGroupId("javax.jms");
		d25.setArtifactId("javax.jms-api");
		d25.setVersion("2.0");
		model.addDependency(d25);
		
		writer.write(new FileOutputStream(new File(baseDir, "/pom.xml")), model);
		
		
	}

	/* (non-Javadoc)
	 * @see com.blink.scm.SCMManager#pushFiles(java.lang.String)
	 */
	public void pushFiles(String repositoryName) throws IOException, NoFilepatternException, GitAPIException {

		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		File f = new File(localWorkspace + PATH_SEPARATOR + repositoryName + PATH_SEPARATOR +".git");

		Repository db = builder.setGitDir(f).findGitDir().build();
		Git git = new Git(db);
		AddCommand add = git.add();
		add.addFilepattern(".").call();

		CommitCommand commit = git.commit();
		commit.setAll(true);
		commit.setMessage("Project Initialized");
		commit.call();  

		String remotePath = remoteRepoUrl +"/git/" +  repositoryName+ ".git";

		PushCommand pushCommand = git.push();
		pushCommand.setRemote(remotePath);
		CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
		pushCommand.setCredentialsProvider(credentialsProvider);
		pushCommand.call(); 
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemoteRepoUrl() {
		return remoteRepoUrl;
	}

	public void setRemoteRepoUrl(String remoteRepoUrl) {
		this.remoteRepoUrl = remoteRepoUrl;
	}

	public String getLocalWorkspace() {
		return localWorkspace;
	}

	public void setLocalWorkspace(String localWorkspace) {
		this.localWorkspace = localWorkspace;
	}
}
