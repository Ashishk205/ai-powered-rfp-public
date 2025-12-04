package com.planet_learning.rest_controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.planet_learning.api_response.ApiResponse;
import com.planet_learning.entity.RequestForPurposal;
import com.planet_learning.record.RfpResRecord;
import com.planet_learning.record.RfpSelectedVendorsReq;
import com.planet_learning.service.RfpService;
import com.planet_learning.service.MailgunEmailService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1/ai/rfp")
public class AIRfpRestController 
{
	private static final Logger log = LoggerFactory.getLogger(AIRfpRestController.class);
	private final ChatClient chatClient;
	private final RfpService rfpService;
	private MailgunEmailService mailGunService;
	
	public AIRfpRestController(
			ChatClient.Builder chatClient,
			RfpService rfpService,
			MailgunEmailService mailGunService) 
	{
		this.chatClient = chatClient
				.defaultSystem("""
						Your job is to undertand and parse the user requirement into a json format.
						DONT INCLUDE This Markdown(```json) IN JSON
						
						The json must follow the same structure as i provide 
						( DONT ASSUME ANOTHER JSON STRUCTURE, JUST FOLLOW MY JSON STRUCTURE)
						{
							items: [
								{itemName: "itemName", itemQuantity: "itemQty", itemSpecification: "itemSpecification"}
							]
							budget: "extract budget or set null",
							deadline_timing: "extract time/data",
							extra_details: "like warrenty, paramters etc.",
							rfp_email_format: "write a professional RFP email draft that i can send to vendors 
							start from just Dear Vendor and in last line of email just mention only company name PlanetLearning( give me only email body not subject, to etc)"
							rfp_email_subject: "write a professional RFP email subject"
						}
						set null in json fields if you didnt find any related field
						""")
				.build();
		this.rfpService = rfpService;
		this.mailGunService = mailGunService;
	}
	
	@GetMapping("/understandUserRequirement")
	public ApiResponse<RfpResRecord> understandUserRequirement(@RequestParam(name = "description") String userPrompt)
	{
		/* to get our extracted json fields from AI response we need to define some
		 * data structure to get our specific data
		* */
		Map<String, Object> res = this.chatClient
		.prompt()
		.user(userPrompt)
		.call()
		.entity(new ParameterizedTypeReference<Map<String, Object>>() {});
		
		if(res == null|| res.isEmpty() || res.get("rfp_email_format") == null) {
			return ApiResponse.error("I couldn't understand your request. Please mention specific items (e.g., '20 laptops').");
		}
		
		// call the service to save the data in DB
		RequestForPurposal proposal = rfpService.saveUserReq(userPrompt, res);
		
		RfpResRecord r = new RfpResRecord(proposal.getId(), rfpService.findAllVendors(), res);
		
		return ApiResponse.success("Data saved and retrieved successfully", r);
	}
	
	@PostMapping("/sendProposalEmailToSelectedVendors")
	public ApiResponse<Boolean> sendProposalEmailToSelectedVendors(@RequestBody RfpSelectedVendorsReq req)
	{
		rfpService.sendProposalEmailToSelectedVendors(req);
		return ApiResponse.success("Email sent successfully", true);
	}
	
	@GetMapping("/testSendEmail")
	public ResponseEntity<String> testSendEmail()
	{
		mailGunService.sendEmail("pandora20595.ak@gmail.com", "Hello from ashish", "testing email thanksworking");
		return null;
	}
	
	@PostMapping("/compareProposalsByRfpIdAndVendorIds")
	public ApiResponse<String> compareProposalsByRfpIdAndVendorIds(@RequestBody RfpSelectedVendorsReq req)
	{
		if(req.rfpId() == null || req.vendorIds().isEmpty()) {
			ResponseEntity.ok(
					Map.of("status", false,
						"message", "Failed",
						"data", null));
		}
		
		return ApiResponse.success("Data retrieved successfully", 
				rfpService.compareProposalsByRfpIdAndVendorIds(req.rfpId(), req.vendorIds()));
	}
}
