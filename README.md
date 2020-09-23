1. Solution accepts any number of CSV, HTML files with any number of columns/rows
2. By default solution merges files by field "id" case insensitive, to change key field set static variable
	COLUMN_TO_MERGE_BY 
3. Current place holder for empty field is " ", to set new placeholder set variable PLACE_HOLDER_EMPTY_FIELD
4. General aproach is to change format of every file into .CSV format, sort them separatly, merge
5. To add support for new file format, one should implement Parser interface and add corresponding constructor
 to Parser Factory

Assumptions
1. Every passed file is correctly formated.
2. HTML parser will look for "directory" element
3. Changed version of OpenCSV library to 4.3.2 and added its dependency commons-lang3-3.11.jar
