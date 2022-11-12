# SimulationCW

FunctionAsAService.Function-as-a-Service (FaaS) plaform simulation.
When a new request for a serverless function arrives, if the contianer is not present in memory the request will be blocked until the container is loaded, introducing an overhead on the request response tim ecalled the cold start time. Instead, if the container is present in memory and can accept the incoming request, the cold start time will not be incurred. Note that the incoming request may be rejected, even though the contined is present in memory.

## Simulation Model

$\lambda _{f}$ : the arrival rate of requests to function f (requests / second ). Assume that inter arrival times for each function are exponentially distributed; hence, the arrival process to each function is a Poisson process

$\alpha = 0.5$ : cold-start overhead parameter (in seconds^-1). As the cold start time depends on vairous factors (machine lod, image size, network bandoidth if the container is retrieved from remote, ...) you should assume that a request for a function f, when it incurs a cold start, suffers an exponentially distributed overhead with mean 1 / alpha before being loaded in memory. Thus, for alpha = 0.5 the average cold start overhead is 2 seconds.

$m _{f}$ : the memory occupation of a loaded function f. Assume this to be identical for all functions and equal to 100 MB.

## Assumptions:

- A0. At time t = 0, no function is in memory.
- A1. If a request arrives for a function f and there is not enough spare memory to load
it, the function g that is already loaded in memory and has been idle the longest is
instantaneously deallocated and its memory space immediately allocated to f. If no
function is idle, the incoming request to f is lost.
- A2. If a request for function f is received while that function is serving another request, then
the incoming request is lost.
- A3. During the cold start period for function f, incoming requests to f other than the one
that triggered the cold start are lost. The request that triggered the cold start can begin
service only after the cold start period ends.
- A4. A function f cannot be deallocated from memory during its cold start. The earliest time
at which it can be deallocated is right after it serves the first job, if f is idle then.
- A5. The CPU capacity is over-provisioned and contention is negligible, so that any request
to function f either (i) receives its intended service time, (ii) receives the service time
plus the cold start time, or (iii) it is lost, based on the rules given above.
- A6. The total memory available is initially 4 gigabytes, i.e. there is capacity for M = 40
functions in memory at any time. To simplify the simulation you are advised to start
the FaaS with the first M functions in memory in the idle state and the remainder in the
unloaded (not in memory) state – this means that there will be precisely M functions
in memory at all times, so you will not have to model unused memory that has yet to
be loaded/initialised.

## Files

/trace-final.csv : real-world serverless dataset tracing F = 10862 functions. For each function f, 1 <= f <= F, the dataset includes:

- the total number of requests that arrived to each function in the observation period of T = 30 days
- the mean service times $S _{f}$ of the functions (in milliseconds, rounded up).

The total number of request invocations allows you to estimate the arrival rate $\lambda _{f}$ for each function. For the purposes of the model you should assume that the service times for function f are exponentially distributed with rate parameter 1 / S_f (i.e. mean S_f) 

## Author(s)

az620 - Anton Zhitomirsky