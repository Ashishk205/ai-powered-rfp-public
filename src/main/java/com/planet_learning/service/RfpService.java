package com.planet_learning.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.planet_learning.AiPoweredRpf01Application;
import com.planet_learning.dao.ProposalRepo;
import com.planet_learning.dao.RequestForProposalRepo;
import com.planet_learning.dao.VendorRepo;
import com.planet_learning.entity.Proposal;
import com.planet_learning.entity.RequestForPurposal;
import com.planet_learning.entity.Vendor;
import com.planet_learning.record.RfpSelectedVendorsReq;
import com.planet_learning.record.VendorRecord;
import com.planet_learning.utils.ProposalStatusEnum;

import jakarta.transaction.Transactional;

@Service
public class RfpService 
{

    private final AiPoweredRpf01Application aiPoweredRpf01Application;
	private static final Logger log = LoggerFactory.getLogger(RfpService.class);
	private RequestForProposalRepo requestProposalRepo;
	private VendorRepo vendorRepo;
	private MailgunEmailService mailgunService;
	private ProposalRepo proposalRepo;
	private final ChatClient chatClient;
	
	public RfpService(
			RequestForProposalRepo requestProposalRepo,
			VendorRepo vendorRepo,
			MailgunEmailService mailgunService,
			ProposalRepo proposalRepo,
			ChatClient.Builder chatClient, AiPoweredRpf01Application aiPoweredRpf01Application) {
		this.requestProposalRepo = requestProposalRepo;
		this.vendorRepo = vendorRepo;
		this.mailgunService = mailgunService;
		this.proposalRepo = proposalRepo;
		this.chatClient = chatClient.build();
		this.aiPoweredRpf01Application = aiPoweredRpf01Application;
	}
	
	public RequestForPurposal saveUserReq(String userPrompt, Map<String, Object> map)
	{
		RequestForPurposal rfp = new RequestForPurposal();
		rfp.setDescriptionRaw(userPrompt);
		rfp.setParsedJsonData(map);
		rfp.setRfpEmailFormat((String)map.getOrDefault("rfp_email_format", null));
		
		log.info("Data: User rfp requirement saved successfully in db.");
		
		return requestProposalRepo.save(rfp);
	}
	
	public List<VendorRecord> findAllVendors()
	{
		List<VendorRecord> records = vendorRepo.findAll()
				.stream()
				.map(v-> {
					return new VendorRecord(v.getId(), v.getName(), v.getEmail(), v.getDescription());
				}).toList();
		
		return records;
	}
	
	@Transactional
	public void sendProposalEmailToSelectedVendors(RfpSelectedVendorsReq req) throws Exception
	{
		// find the vendors and rfpById
		List<Vendor> vendors = vendorRepo.findAllVendorsByIds(req.vendorIds());
		
		// find rfpById
		Optional<RequestForPurposal> rfpOptional = requestProposalRepo.findById(req.rfpId());
		
		if(rfpOptional.isEmpty()) {
			log.debug("No rfp present");
			return;
		}
		
		Map<String, Object> rfpParsedJsonData = rfpOptional.get().getParsedJsonData();
		// extract subject and email from rfp parsedJsonData
		String emailSubject = (String) rfpParsedJsonData.get("rfp_email_subject");
		String emailBody = (String) rfpParsedJsonData.get("rfp_email_format");
		
		log.info("emailSubject: {}", emailSubject);
		
		// send email to allSelectedVendors
		vendors.forEach(v-> {
			
			try {
				// send email to vendors
				mailgunService.sendEmail(v.getEmail(), emailSubject, emailBody, rfpOptional.get().getId());
				
				// make proposal record in db
				Proposal p = new Proposal();
				p.setRfp(rfpOptional.get());
				p.setVendor(v); // to
				p.setEmailContentRaw(emailBody); // email body what we sent just now
				p.setStatus(ProposalStatusEnum.SENT); // we just sent email
				proposalRepo.save(p);
			} catch(Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	// when user clicks on analyze button, then against this proposal we have multiple vendors
	// who received this email and reply on this email
	public String compareProposalsByRfpIdAndVendorIds(Long rfpId, List<Long> vendorIds)
	{
		String prompt = """
				Yor are an professional procurement manager whose task is to compare REQUEST FOR PROPOSALS responses and 
				based on vendor responses make decisions, find the best propsal,try to give overall score
				,give summary details after analysis and in last recommendation(who is best) based on propsal's.
				
				Your Task:
				1. Give a comparision in html table (Format all data consistently)
				2. predict the best propsal, give overall score, give summaries after analysis
				3. and score out of 100 for each and in last recommendation(who is best)
				4. Dont assume anything see only the present context
				5. if you are unable to compare say 0 score from my analysis i didnt find any relvent information
				6. In HTML table this last row must be 'overall score' and recommendation with reason why and why not.
				
				NOTE compare based on some details like if you found: (price, warrently, delivery time, any extra details)
				NOTE if you found only one proposal say something like: 
				i have only one proposal no data for comparision, wait for another vendors to reply
				""";
		
		// find Proposals
		List<Proposal> proposals = 
				proposalRepo.findAllByRfpIdAndVendorIdsAndStatus(rfpId, ProposalStatusEnum.RECEIVED, vendorIds);
		
		// get all proposal in String format
		StringBuilder promptData = new StringBuilder();
		promptData.append("Compare these vendor proposals for RFP ID: ").append(rfpId).append("\n\n");
		
		for(Proposal p: proposals) {
			promptData.append("Vendor: ").append(p.getVendor().getName()).append("\n");
			promptData.append("Vendor Proposal: ").append(p.getParsedVendorRes()).append("\n");
			promptData.append("------------------------------").append("\n");
		}

		log.info("Vendors Proposals: {}", promptData.toString());
		
		/* This is only for practice
		ChatResponse chatRes = this.chatClient
				.prompt()
				.system(prompt)
				.user(promptData.toString())
				.call()
				.chatResponse();
		String text = chatRes.getResult().getOutput().getText();
		*/
		// To keep it simple
		
		return this.chatClient
				.prompt()
				.system(prompt)
				.user(promptData.toString())
				.call()
				.content();
	}
}