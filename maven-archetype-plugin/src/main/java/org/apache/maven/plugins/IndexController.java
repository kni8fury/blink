package org.apache.maven.plugins;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;

@Controller
public class IndexController {

	@Path("/")
	public String index() {
		return "You are ready to use VRaptor";
	}
}
