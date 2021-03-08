# ConwaysGameOfLifeDesktop

## WARNING
Running the real time simulation with unknown rule numbers may cause the canvas to blink as cells transition from alive to dead and back each time. PLEASE BE SURE THAT YOU TEST AN UNKNOWN RULE TYPE BY MANUALLY SIMULATING A FEW GENERATIONS BEFORE PRESSING PLAY!

## Explore the World of Cellular Automata!
Cellular automata are simple automata that can take on two states: alive or dead (or any other dichotomies you can think of). These cellular automata tend to live in either a 1d or 2d plane with their peers, and as time passes they are born and die according to simple rules. These rules determine, for each cell in the plane, whether the cell should be alive (based on the contents of itself and its neighbors) or dead. Although these rules are generally very simple, they can produce interesting, complex behavior when applied iteratively to a set of initial conditions.

This application, written in Java12 using JavaFX15, is a cellular automata visualizer and simulator that allows you to explore the world of simple cellular automata!

## So What's the Basis for These Automata?
This program makes use of the definitions of Cellular Automata provided in the first few chapters of Stephen Wolfram's book [A New Kind of Science](https://www.wolframscience.com/). This program was built as an excercise in implementing the simplest cases of these sorts of simple automata.

## Installing the Visualizer
The visualizer may be run by forking this repo and running:
  ./gradlew run
  
Java JDK12 must be available on the host machine.

## What Does the Visualizer Do?
The first tab, "Automata Visualizer" allows for real time visualization of a type and rule for cellular automata.
![RealTimeSimulation](https://user-images.githubusercontent.com/50113756/110374165-f485a580-8015-11eb-849d-810b0372b4d0.png)  
Automata options appear in the pane on the bottom right, and are the following:
  
* Automata Type: Choose between different automata types, discussed later in this document.
* Rule Number: The binary representation of the rule which will be used to update cells as the simulation proceeds. Derived from A New Kind of Science's description of rule number.
* Initial Conditions: Supplies an initial condition for the universe to be simulated.

In the second tab, the "Automata Generator" generates an automata. The only new option here is the ability to specify the exact number of generations to simulate. Simulation occurs behind the scenes, and only the final universe is displayed. A note that our universe is not infinite, and cells beyond the edge are taken to be dead. This means that some rules will create "edge" structures that would not exist in a normal universe. An example appears below:![EdgeStructure](https://user-images.githubusercontent.com/50113756/110382761-f9038b80-8020-11eb-813d-32617b08bebb.png)  
  
In an infinite universe, the "structures" at the corners would not exist, as they are an artifact of not simulating on an infinite plane. To this end, it is recommended to keep the number of generations of under half the size of the canvas (for a 500px canvas, fewer than 250 generations) to avoid these edge artifacts from interacting with naturally growing centered structures.

### 1D Time Series
The 1D Time Series option instantiates a 1D plane in which each row in the next generation of the automata. This allows for the user to view the change in the 1d line of automata over time, and reveals some interesting results. In particular, Rule 30 (identified in A New Kind of Science) shows irregularity and unpredictability mixed with ordered structures.

### 2D Time Series 4 Neighbor
This 2D Time Series option instantiates a 2D plane in which all cells are updated at each simulation step according to the rule. Each cell checks its four non-diagonal neighbors.

### 2D Time Series 8 Neighbor
This 2D Time Series is the same as the 4 neighbor one, except that each rule derived checks all 8 neighbors, and as such there are many more rules. Rule 224 here is Conway's Game of Life

### Conway's Game of Life
A special circumstance of Time Series 8 Neighbor with rule 224.

### Some Fun Screenshots
There's a tremendous amount to explore! Here are some interesting automata I've found, as well as the famous "Rule 30". The generator provides the ability to save generated automata. Try to find new interesting ones yourself!
#### The Famous "Rule 30"
![Rule30Screenshot](https://user-images.githubusercontent.com/50113756/110383187-8e068480-8021-11eb-93d4-aad7890e738e.png)

#### Various Interesting Results
![CellularAutomata](https://user-images.githubusercontent.com/50113756/110383167-8a72fd80-8021-11eb-80b1-116024265f60.png)
![8NeighborRule](https://user-images.githubusercontent.com/50113756/110383161-88a93a00-8021-11eb-8965-5d4232e5181c.png)
![Interesting1](https://user-images.githubusercontent.com/50113756/110383615-2997f500-8022-11eb-9816-6e1da686bfae.png)
![Interesting2](https://user-images.githubusercontent.com/50113756/110383616-2997f500-8022-11eb-8531-24c95cdae256.png)
