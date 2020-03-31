# Homework 3
# Angel Ramirez
### Description: object-oriented design and implementation of the name clashing functionality in the IntelliJ plugin with the design pattern code generator from homeworks 1 and 2.

# To install
- Open the project folder on the terminal and run gradlew buildPlugin
- IntelliJ go to file -> settings -> plugins -> the gear icon -> install plugin from disk
- Restart IntelliJ

# To use
The plugin will be located under "Tools" on the menu bar

In order for the name clashing functionality to work, make sure to select the src directory before using the plugin
to create the new files for the design pattern otherwise the name clashing functionality would not work.

#Important Notes
The current implementation of this project does not allow...
- the creation of all 8 design patterns initially required from the first assignment.
- the check for the default value names
does not include...
- unit tests

The current implementation of this project does allow...
- the checking for repeated names in multiple files
- the creation of 4 design patterns


## Evaluation criteria
- the maximum grade for this homework is 8% with the bonus described above. Points are subtracted from this maximum grade: for example, saying that 2% is lost if some requirement is not completed means that the resulting grade will be 8%-2% => 6%; if the core homework functionality does not work, no bonus points will be given;
- only some POJO classes are created and nothing else is done: up to 7% lost;
- having less than five unit and/or integration tests: up to 5% lost;
- missing comments and explanations from the submitted program: up to 5% lost;
- logging is not used in your programs: up to 3% lost;
- hardcoding the input values in the source code instead of using the suggested configuration libraries: up to 4% lost;
- no instructions in README.md on how to install and run your program: up to 5% lost;
- the plugin crashes without completing the core functionality: up to 6% lost;
- no design and modeling documentation exists that explains your choices: up to 6% lost;
- the deployment documentation exists but it is insufficient to understand how you assembled and deployed all components of the program: up to 5% lost;
- the minimum grade for this homework cannot be less than zero.

Enjoy!!