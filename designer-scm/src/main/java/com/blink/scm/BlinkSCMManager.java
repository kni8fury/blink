package com.blink.scm;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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

public class BlinkSCMManager implements SCMManager {

	private static final String PATH_SEPARATOR = File.separator;
	
	private static final Logger logger = Logger.getLogger(BlinkSCMManager.class);

	static {

		System.setProperty("javax.net.ssl.keyStore","/Users/asing20/gitblit/data/serverKeyStore.jks");
		System.setProperty("javax.net.ssl.trustStore", "/Users/asing20/gitblit/data/serverTrustStore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "gitblit");
		System.setProperty("javax.net.ssl.trustStorePassword", "gitblit");
		System.setProperty("maven.home", "/Users/asing20/maven/");
	}


	private GitblitClient client;
	private String username; 
	private String password; 
	private String remoteRepoUrl; 
	private String localWorkspace; 

	public BlinkSCMManager() {

	}

	/* (non-Javadoc)
	 * @see com.blink.scm.SCMManager#init()
	 */
	public void init() throws IOException  {
		GitblitRegistration registration = new GitblitRegistration("test",remoteRepoUrl,username,password.toCharArray());
		client = new GitblitClient(registration);

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
	public String createLocalRepository(String repositoryName) throws GitAPIException, RepositoryExistsException {

		final String localRepo = localWorkspace + PATH_SEPARATOR + repositoryName;
        
		InitCommand init = Git.init();
		File initFile = new File(localRepo);
		if(initFile.exists()) 
			throw new RepositoryExistsException("Local Repository " + repositoryName+ "already exists in Workspace");
		initFile.mkdirs();
		init.setDirectory(initFile);

		init.call();
		
		createMavenProject(localRepo, repositoryName);
		
		return localRepo;

	}

	private void createMavenProject(String projectDirectory, String projectName) {
		MavenCli cli = new MavenCli();
		int result = cli.doMain(new String[]{"archetype:generate","-DgroupId=com.mycompany.app", "-DartifactId="+ projectName,"-DarchetypeGroupId=org.apache.maven.archetypes", 
				"-DarchetypeArtifactId=maven-archetype-quickstart", "-DarchetypeVersion=1.1","-DinteractiveMode=false"},
				projectDirectory,  System.out, System.out);
		
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
