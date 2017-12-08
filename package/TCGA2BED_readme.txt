  _______ _____ _____          ___  ____  ______ _____  
 |__   __/ ____/ ____|   /\   |__ \|  _ \|  ____|  __ \ 
    | | | |   | |  __   /  \     ) | |_) | |__  | |  | |
    | | | |   | | |_ | / /\ \   / /|  _ <|  __| | |  | |
    | | | |___| |__| |/ ____ \ / /_| |_) | |____| |__| |
    |_|  \_____\_____/_/    \_\____|____/|______|_____/ 
  
#########################################################################


This is the Readme for the TCGA2BED software tool, suitable for
the download and conversion of TCGA genomic and clinical data 
to BED format.
It introduces the TCGA2BED package and explains how to
run and use the program.

=========
Package
=========

	TCGA2BED_readme.txt -- this file
	TCGA2BED.jar -- the Java application
	appdata -- contains some files needed for a proper software execution,
			   in particular three different dataset from external public 
			   databases useful for the retrieval operation of genomic
			   coordinates
	lib -- contains the Java application dependencies
	config-convert-example and config-download-example -- two XML example
			   files to automate the download and conversion of TCGA data.
			   See the TCGA2BED User Guide for a better explanation of
			   theese files.


=========
Execution
=========

The provided TCGA2BED.jar is a runnable Java application.
The program needs Java 1.8 that is freely available for download from 
Oracle website http://www.oracle.com/technetwork/java/javase/downloads/
To start running the application, the folders named 'appdata' and
'lib' have to be in the same directory of TCGA2BED.jar.
TCGA2BED can be run by double clicking it, or with the command line
instruction "java -jar TCGA2BED.jar" if the Java installation path
is in the PATH and CLASSPATH environment variables of the operating
system in use.
For a proper software execution, an active internet connection is required.


==========
How To Use
==========

The application is composed of two sections responsible
for the download and the conversion of TCGA genomic and related 
clinical data.

--------------------
TCGA2BED Downloader
--------------------

The left-hand side of the application window allows the download of 
genomic and clinical data from TCGA by selecting the "Experiments" 
or "Metadata" radio buttons, respectively. 
For downloading the requested data the user has to seelect the desidered
disease by scrolling the drop-down menu.
By clicking the "Download" button the user has to select the folder 
wher the requested data set will be downloaded and the retrieval will 
start.

-------------------
TCGA2BED Converter
-------------------

The right-hand side of the application window allows the conversion
of genomic data from the TCGA format to the free BED standard format.
The information needed for this operation are the disease and the
data type of the data that will be converted.
Before clicking the "Convert" button, it's necessary to specify the
folders containing clinical and genomic data of the considered tumor 
and experiment type.
By clicking the "Convert"  button the user has to select the folder 
where the specified data set will be converted.


=========
Licensing
=========

TCGA2BED is freely available under the GPL License.
The software includes the Apache Commons Compress library 
that is provided under the Apache License.
The package may be redistributed as long as it is intact and
unmodified.


=========
Contacts
=========
Fabio Cumbo: fabio.cumbo@iasi.cnr.it