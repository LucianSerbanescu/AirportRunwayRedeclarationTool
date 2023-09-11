# Airport Runway Redeclaration Tool

## Task Description

### Background 

Commercial airports are busy places. Ideally runways will be fully open at all times, but this is not always possible. When there is an obstruction (such as a broken down aircraft or surface damage) on the runway, it may need to be closed. However it may still be possible to keep the runway open, albeit with reduced distances available for landing and taking off.

All runways have a published set of parameters. When an obstacle is present on the runway, these parameters must be recalculated and a commercial decision made whether to continue operations on the runway. If (limited) operations are to continue, the published data about the runway must be recalculated and republished. The final decision about whether to land/take off rests with the pilot.

### Problem 

The calculations and process to determine runway parameters are specified by the CAA. The calculations must be completed independently by two competent (qualified) people who must then reconcile their results making the task involved and time consuming.

The customer desires a tool which, given standard runway information and information about an obstacle, provides the revised runway parameters together with a visualisation of the obstacle and a summary of the calculations. The tool will be used to obtain a rapid indication of the effect of an obstruction on the runway parameters as an aid to deciding whether operations can continue, and whether performing the calculations in accord with the official process is worthwhile. The tool will be used as an aid/guide. It cannot replace the official process.


## Customer requirements 

### Mandatory

1) The system should be configurable to permit its use at any UK commercial airport. 
<p align="center"><img width="350" src="https://github.com/LucianSerbanescu/AirportRunwayRedeclarationTool/blob/master/app%20screenshots/Screenshot%202023-09-11%20at%2012.06.54%20pm.png?raw=true"/></p>

2) The tool must provide at least two visualisations of each of the airports, including 2D top-down and side-on views. These may be displayed simultaneously or individually. <p align="center"><img width="350" src="https://github.com/LucianSerbanescu/AirportRunwayRedeclarationTool/blob/master/app%20screenshots/Screenshot%202023-09-11%20at%2012.07.16%20pm.png?raw=true"/></p>
3) The system must calculate the new runway distances available when ONE obstacle is present, given the obstacle’s distances from each threshold, distance from the centreline and height. <p align="center"><img width="350" src="https://github.com/LucianSerbanescu/AirportRunwayRedeclarationTool/blob/master/app%20screenshots/Screenshot%202023-09-11%20at%2012.07.29%20pm.png?raw=true"/></p>
4) The program should come with a list of predefined obstacles.
5) The user must be able to view the re-calculated values and the originals.
6) The user must be able to view a breakdown of the calculations so they may be compared with the paper results. <p align="center"><img width="350" src="https://github.com/LucianSerbanescu/AirportRunwayRedeclarationTool/blob/master/app%20screenshots/Screenshot%202023-09-11%20at%2012.08.05%20pm.png?raw=true"/></p>
7) The system should be able to import and export details of obstacles, airports and other data using appropriate XML files.
8) Both views must be able to display:
• The runway strip.
• Threshold indicators.
• Threshold designators e.g. 27R or 09L, with the letter below the number.
• Any displaced thresholds that are present.
• Stopway / Clearway for both ends of the runway.
• Indication of the take-off / landing direction.
• All re-declared distances, with indicators showing where they start and end relative
to the runway strip.
• The distances should be broken down into their respective parts, including
RESA/Blast Allowance.
• The obstacle, if one is present upon the runway.
• The offset caused by the RESA and slope angles relative to the obstacle on the
runway.
The top-down view must be able to display:
• The runway centreline. <p align="center"><img width="350" src="https://github.com/LucianSerbanescu/AirportRunwayRedeclarationTool/blob/master/app%20screenshots/Screenshot%202023-09-11%20at%2012.07.59%20pm.png?raw=true"/></p> <p align="center"><img width="350" src="https://github.com/LucianSerbanescu/AirportRunwayRedeclarationTool/blob/master/app%20screenshots/Screenshot%202023-09-11%20at%2012.07.22%20pm.png?raw=true"/></p>
9) The lower threshold, which is the threshold that has the lowest value, should always be located on the left. For example, in runway 09L/27R, 09L must be on the left.
10) There should be an option to automatically rotate the runway strip to match its compass heading.
11) The top-down view must also display the Cleared and Graded areas around the runway strip.
12) The side-on view must also display a representation of the TOCS (Take-Off Climb Surface) / ALS (Approach / Landing Surface) slope caused over the obstacle when one is present.
13) The user must be able to select different runways and thresholds, with the views updating upon their selection.
14) The system must be able to display notifications to the user indicating any actions that have taken place e.g. obstacle added, runways re-declared, values changed, etc.

### Optional

1) A Map view with the runway overlaid over real-world images.
2) The ability to zoom and pan the views.
3) A 3D visualisation of the airfield.
4) Be able to export the displays in different formats including JPEG, PNG, GIF, etc.
5) Provide API support for the use of assistive technologies, such as screen readers.
6) Provide alternative colour schemes for your program, which allow colour-blind users to view it.
7) <p align="center"><img width="350" src="https://github.com/LucianSerbanescu/AirportRunwayRedeclarationTool/blob/master/app%20screenshots/Screenshot%202023-09-11%20at%2012.06.56%20pm.png?raw=true"/></p>
8) Be able to print out the results of the currently viewed situation in a textual format.
9) Any other useful extensions that you can think of. These must be clearly stated and
explained in your final report.
