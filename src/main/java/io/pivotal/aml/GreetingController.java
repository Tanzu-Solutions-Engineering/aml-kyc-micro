package io.pivotal.aml;

import io.pivotal.aml.pushnotificatin.MessageSender;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

	static Logger logger = Logger.getLogger(GreetingController.class);
    private static final String template = "Payment to  %s Confirmed";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", required=false, defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
    
	@RequestMapping(value = "/pymt", method = RequestMethod.GET)
	 public String transact(@RequestParam(value="name", required=false, defaultValue="Vendor") String name) {
        String amt,vendor,msg;
        int ct;
		vendor = name.substring(0, name.indexOf(":"));
		amt = name.substring(name.indexOf(":")+1,name.lastIndexOf(":"));
		ct = Integer.parseInt(name.substring(name.lastIndexOf(":")+1));
		long cnt = counter.incrementAndGet();
		String url = "http://push-notifications.cfdemo5.fe.gopivotal.com/";
		//logger.warn("Count :" + counter);
		if ((ct % 3)==0) {
			logger.warn("COMPLIANCE ISSUE: Payment Request for "+ amt +" should not be sent to " + vendor );
			//msg = "Payment Request for "+ amt +" to be sent to " + vendor + " has been approved by your Bank.";
			msg = "Payment not approved. Check notifications from your Bank.";
			MessageSender m = new MessageSender(vendor, amt, url);
			m.sendMessage();
		} else {
			logger.warn("Received Payment Request for "+ amt +" to be sent to " + vendor );
			logger.warn("Prcossed " + cnt + " payments.");
			msg = "Payment Request for "+ amt +" to be sent to " + vendor + " has been approved by your Bank.";
		}

		return msg;
    }
}
