import javax.jms.Queue;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractAction {


	@Autowired
	EntityManager em;
	
	@Autowired
	Queue queue;
		
	abstract public boolean createAction(Object obj);
	abstract public boolean readAction(Object obj);
	abstract public boolean updateAction(Object obj);
	abstract public boolean deleteAction(Object obj);
	}

