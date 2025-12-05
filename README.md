Technology Stack Decisions:
I chose Spring Boot for the following strategic reasons:

Spring AI Integration: I leveraged Spring AI, which provides a unified abstraction layer for interacting with LLMs. This allows for cleaner code separation between the business logic and the AI provider. It also makes the system "future-proof"—switching from OpenAI to Anthropic or Bedrock would only require a configuration change, not a code rewrite.

Mature Ecosystem for Integration: The workflow relies heavily on email integration. The Java ecosystem (via JavaMailSender and standard libraries) offers battle-tested stability for SMTP/IMAP handling compared to the more fragmented NPM ecosystem.

Developer Proficiency: I am highly experienced with the Spring ecosystem. Using the tool I have mastered allowed me to focus purely on the architectural complexity (AI agents, Webhooks, Data Modeling) rather than fighting syntax, resulting in a higher-quality deliverable within the timeframe.

Frontend: Angular (Standalone Components) instead of React
"Batteries-Included" Architecture: Angular provides a standardized way to handle Routing, HTTP Clients, and Forms out of the box. For a single-user workflow app that requires a "Wizard" style interface (Create -> Select -> Send), Angular's structured approach resulted in cleaner, more maintainable code than assembling a React stack from scratch.

Key Assumptions & Architecture:
AI as the Parser (Zero-Shot Extraction):

Assumption: I assume the vendor responses will be in natural language (English) but "messy" (mixed with signatures, greetings, etc.).

Decision: Instead of writing brittle Regex parsers, I rely entirely on the LLM's "Context Window" capabilities. I feed the raw email body to the AI with a strict system prompt to extract JSON. This handles the requirement to "update your system automatically, without the user manually keying in each number".

Vendor Selection Workflow:

Assumption: The user needs to verify the AI's understanding of the requirements before contacting vendors.

Decision: The flow is designed as: Prompt -> Preview AI Structure -> Select Vendors -> Send. This "Human-in-the-loop" step prevents the AI from hallucinating requirements and sending incorrect RFPs to real vendors.

Single-User Scope:

As per the "Non-Goals", no authentication or multi-tenancy was implemented. The system assumes a single Procurement Manager operating locally.
