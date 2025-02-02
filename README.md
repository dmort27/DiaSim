# DiaSim

This package, or rather earlier stages of it, are covered broadly in Marr & Mortensen 2020 (preprint: <https://www.academia.edu/71888418/Computerized_Forward_Reconstruction_for_Analysis_in_Diachronic_Phonology_and_Latin_to_French_Reflex_Prediction>), and Marr & Mortensen 2022 (preprint: <https://www.academia.edu/94911785/Large_scale_computerized_forward_reconstruction_yields_new_perspectives_in_French_diachronic_phonology>)

## Wiki pages

This README file covers basic technical information, such as how to get DiaSim up and running. For detailed information on usage of DiaSim, see the following pages in this repository's wiki:

- [Lexicon](https://github.com/clmarr/DiaSim/wiki/Lexicon): covers how to build your lexicon file, the set of etyma to realize sound changes upon, including how to make a lexicon file with columns for ('gold') stages with observed forms to compare with reconstructed outcomes, and how include paradigmatic information and token frequencies.

- [Cascade](https://github.com/clmarr/DiaSim/wiki/Cascade): covers how to make your cascade file, the set of ordered sound changes to realize upon your lexicon, according to SPE format with a couple added gimmicks, the use of alpha features, the placement of gold and black stages in the cascade, and so forth.
  
- [Representations](https://github.com/clmarr/DiaSim/wiki/Representations): covers the handling of features and the phone symbols defined in terms of them, including feature implications and translations.

- [Metrics](https://github.com/clmarr/DiaSim/wiki/Metrics): covers how metrics provided whenever DiaSim evaluates reconstructed outputs against observed forms are computed, and how to modify their computation.

- [Suite](https://github.com/clmarr/DiaSim/wiki/Suite): covers the diagnostics offered by DiaSim whenever it reaches a halting point, and how to use the suite menu to "debug" your cascade!

## Running DiaSim

### Windows Command Line

On Windows, DiaSim can be run with the batch script `derive.bat`.

In the command line, navigate to the DiaSim directory and run derive.bat by using the following command:

```text
 ./derive.bat
```

Command line arguments can be included here; see the "Command line arguments" section below.

### Bash (Linux / Mac OS)

On Linux and Mac OS, DiaSim can be run with the bash script `derive.sh`.

In the command line, navigate to the DiaSim directory and run derive.sh by using the following command:

```text
 ./derive.sh
```

Command line arguments can be included here; see the "Command line arguments" section below.

## Command line arguments

You may include these command line arguments by adding them to your run command. If you run the script without any arguments, DiaSim will default to using FLLAPS for its lexicon and DiaCLEF for its cascade, and the output will go to a folder with the name `unnamed_run_<datetime>`.

Specifying a lexicon, cascade, and run name:

- `-lex <filename>` -- sets the file with the etyma to implement sound changes on (see the [**Lexicon**](https://github.com/clmarr/DiaSim/wiki/Lexicon) page of the wiki)
  
- `-rules <cascade_file>` -- sets the file with the ordered sound changes to realize upon the lexicon

- `-out <run_name>`, where <run_name> is the name you want the folder with all resulting forward-reconstructions and analysis files to be placed

Additional options:

- `-symbols <symbol_file>`  -- allows you to use a symbol definitions file other than symbolDefs.csv (on how to make these, you can follow the rubric of that file and/or consult the [**Representations**](https://github.com/clmarr/DiaSim/wiki/Representations) page of the wiki)
  
- `-impl <filename>` -- allows you to use a feature implications file other than the default FeatureImplications (cf. [**Representations**](https://github.com/clmarr/DiaSim/wiki/Representations) on the wiki)
  
- `-diacrit <filename>` -- allows you to use a custom diacritics file (cf. [**Representations**](https://github.com/clmarr/DiaSim/wiki/Representations) on the wiki). If you use just `-diacrit`, the standard diacritics file will be used. 
  
- `-idcost <a number>` -- sets the cost of insertion and deletion for computing edit distances (cf. [**Metrics**](https://github.com/clmarr/DiaSim/wiki/Metrics) on the wiki)

- `-verbose` -- verbose mode -- prints out more information about file locations and other variables set at the command line call.

There are also the following command line flags, which are put together after a single hyphen (eg. "`-ph`")
  
- `-p` -- print changes mode -- prints words changed by each rule to console as they are changed.
  
- `-h` -- halt mode --- halts at all intermediate stages, not just those associated with observed outcomes to test against (gold stages)
  
- `-e` -- explicit mode -- ignores feature implications

- `-s` -- skip file creation -- runs without creating a run output folder

### Example configuration

Suppose you have created a lexicon named `my_lexicon` and a cascade named `my_cascade`. You have put these files in the DiaSim directory. You want to run DiaSim and have it make an output folder called `my_run`. Additionally, you want DiaSim to print to the console every sound change and affected etymon as it runs. Once you have navigated to the DiaSim directory, you will run a shell command that looks like this:

Windows command line:

```text
./derive.bat -lex my_lexicon -rules my_cascade -out my_run -p
```

Bash:

```text
./derive.sh -lex my_lexicon -rules my_cascade -out my_run -p
```

## Lexicon file

A lexicon file contains your collection of etyma to be processed. It contains a series of attested lexical items from a starting point ("input stage"), optionally followed by any number of series of attested forms of those words from later stages ("gold stages"). DiaSim iterates through your cascade of rules (see below), applies changes to this lexicon, and computes output forms, which can be compared to your gold stages. DiaSim can provide details on any discrepancies between computed results and what is actually observed in the gold stages.

More information on lexica can be found on the [**Lexicon**](https://github.com/clmarr/DiaSim/wiki/Lexicon) page of the wiki.

## Cascade file

A cascade file contains your ordered list of sound change rules that you desire DiaSim to apply to the lexicon. If your lexicon includes intermediate gold stages (see "Creating a lexicon file" above), then the point at which these stages occur must also be specified in the cascade. Additionally, stages without attested forms can be specified in the cascade; these are called "black stages." Since black stages, by definition, do not have corresponding attested forms which appear in the lexicon, black output forms cannot be cross-checked. They simply serve to let DiaSim know what other notable points in the cascade may be.

The rules that make up a cascade are in the conventional sound change notation, that is to say, `A > B / C __ D`.

More information on cascades can be found on the [**Cascade**](https://github.com/clmarr/DiaSim/wiki/Cascade) page of the wiki.

## Output data

DiaSim will populate your chosen output folder with files containing information on your results. These files include a log of the rules applied, a table of etyma in the state they appear at each stage, statistical analyses pertaining to phones, and a folder containing step-by-step forward-reconstructions for each etymon.
