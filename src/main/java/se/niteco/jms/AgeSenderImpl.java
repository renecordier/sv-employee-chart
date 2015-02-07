package se.niteco.jms;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component("ageSender")
public class AgeSenderImpl implements AgeSender {
	
	@Autowired
	private static JmsTemplate jmsTemplate; //DI of jmstemplate
	
	public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }
 
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
    	AgeSenderImpl.jmsTemplate = jmsTemplate;
    }
    
    /**
     * Public constructor
     */
    public AgeSenderImpl() {
		
	}
    
    /**
     * Public constructor
     * @param jmsTemplate
     */
    public AgeSenderImpl(JmsTemplate jmsTemplate) {
    	AgeSenderImpl.jmsTemplate = jmsTemplate;
	}

	public void sendEmployeeAges(List<Integer> ages) {
		// TODO Auto-generated method stub
		System.out.println("Sending JMS Message");
        System.out.println(ages);
 
        try {
       	 jmsTemplate.convertAndSend(ages);
        } catch (Exception e) {
       	 e.printStackTrace();
        }
 
        System.out.println("Message sent.");
	}

}
