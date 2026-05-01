### A public-facing REST API that provides the latest snapshot prices for global financial assets.

### The Endpoints:

GET ``/api/v1/prices/{ticker}`` (Returns a single JSON object with the current price of an asset, like "BTCUSDT").

### The Traffic & Tiers:

### Free Users: 
Allowed 10 HTTP requests per minute.

### Pro Users (Algorithmic Traders): 
Allowed 1,000 HTTP requests per second.

### Scale: 
Assume the system handles 50,000 requests per second globally across all users.

The Architecture Challenges to Solve:
Now that we know we are dealing with HTTP GET requests, here is what you need to design:

### Placement: 
Where in your architecture does the rate-limiting code physically execute? Does it happen inside your Spring Boot application (e.g., via a HandlerInterceptor), or does it happen before it even reaches your app (e.g., an API Gateway like Spring Cloud Gateway, Nginx, or Kong)? What are the trade-offs?


### Distributed State: 
You have 10 identical Spring Boot servers running in different regions. If a Free User sends 5 requests to the server in London and 5 requests to the server in New York, they have hit their 10-request limit. How do the servers share this count so the 11th request is correctly blocked globally?

### Performance Isolation: 
The background thread that updates the ConcurrentHashMap with fresh market data must run in microseconds. How do you ensure that millions of incoming rate-limit checks (and potentially waiting on network calls to a shared cache) do not block the thread that serves the actual price data?
