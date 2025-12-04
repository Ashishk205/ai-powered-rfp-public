package com.planet_learning.rest_controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.planet_learning.dao.ProposalRepo;
import com.planet_learning.dao.VendorRepo;
import com.planet_learning.entity.Proposal;
import com.planet_learning.entity.Vendor;
import com.planet_learning.utils.ProposalStatusEnum;

import jakarta.transaction.Transactional;


@RestController
@RequestMapping("/api/v1/webhooks")
public class MailgunWebhookRestController 
{
	private static final Logger log = LoggerFactory.getLogger(MailgunWebhookRestController.class);
	private ChatClient chatClient;
	private VendorRepo vRepo;
	private ProposalRepo pRepo;
	
	public MailgunWebhookRestController(
			ChatClient.Builder chatClient,
			VendorRepo vRepo,
			ProposalRepo pRepo)
	{
		this.chatClient = chatClient.build();
		this.vRepo = vRepo;
		this.pRepo = pRepo;
	}

	@PostMapping("/mailgun")
	@Transactional
	public void handleIncomingEmail(
			@RequestParam("sender") String sender,
            @RequestParam("subject") String subject,
            @RequestParam(value = "body-plain", required = false) String body,
            @RequestParam Map<String, String> allParams)
	{
		log.debug("mailgun webhook received");
		
		System.out.println("=== NEW INCOMING EMAIL ===");
        System.out.println("From: " + sender);
        System.out.println("Subject: " + subject);
        //System.out.println("Body: " + body); // 'stripped-text' creates a clean reply
        
        // findVendor
        Optional<Vendor> vOptional = vRepo.findByEmail(sender);
        
        if(vOptional.isEmpty()) {
        	throw new RuntimeException("Vendor not found");
        }
        
        // find proposal by vendorId and email status sent
        Optional<Proposal> pOptional = pRepo.findByVendorIdAndStatus(vOptional.get().getId(), ProposalStatusEnum.SENT);
        
        if(pOptional.isEmpty()) {
        	throw new RuntimeException("Proposal not found");
        }
        Proposal p = pOptional.get();
        
        Map<String, Object> parsedRes = parsedVendorRes(body);
        
        // update the proposal now
        p.setStatus(ProposalStatusEnum.RECEIVED); // he reply means vendor received our email
        p.setParsedVendorRes(parsedRes);
        pRepo.save(p);
        
        log.debug("propsal update successfully using mailgun webhook");
	}
	
	public Map<String, Object> parsedVendorRes(String vendorRes)
	{
		String template = """
				You are data extractio engine
				Analyze and extract response data and convert it into JSON format
				
				DONT INCLUDE This Markdown(```json) IN JSON
						
				The json must follow the same structure as i provide
				and if you think any field i miss then please add that also in my json structure
				{
					final_price_quote: "extract the deal price ( number or string ) if not present put null",
					delivery_time: "extract delivery time related (time related info)",
					warrenty_terms: "extract trust, warrenty if not there put null",
					summary: "is there conditions terms etc",
					extra_details: "is you find any relavent information"
				}
				If a field is missing set to null
				give a valid json
				""";
		
		Map<String, Object> res =  this.chatClient
		.prompt()
		.system(template)
		.user(vendorRes)
		.call()
		.entity(new ParameterizedTypeReference<Map<String, Object>>() {});
		
		return res;
	}
}

