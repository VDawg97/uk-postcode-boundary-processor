## **UK Postcode Area & District Boundary Finder**
- **DataFinder** finds boundaries of your specified postcode area and "spits out" a collection of *postcodes* which belong to the postcode area, in a single file. Each postcode is attached with a property *title* which describes the area (note: this may be Post Town, District or LAD name).
- **DataMerger** reads your provided *geojson* files and puts them together under one *FeatureCollection* in a single file.

The generated result files are in **GEOJson** format.

### Instructions
**DataFinder**
- Run *DataFinder*.
- You will be asked to provide a name for the result directory - enter one.
- Then, you will be requested to enter a postcode area and a name you would like to give to the result file e.g. PL,Plymouth (no space on either side of the comma symbol).
- A file will be generated for the previous step and you will be prompted to enter another postcode area, continuously.
- You may stop *DataFinder* when you have collected the data you need.
- The result files will be located under in *src/main/resources/results*.

For example, if you specified directory name to be *London*, then the results will be inside *London* directory created in the path mentioned previously.

The directory will also contain *districts* directory. Inside you will find a directory for each *postcode area name* that you have entered with *geojson* files for each *postcode district* that belongs to that area.

**DataMerger**
- Place *GeometryCollection* files inside *merge_data* directory located in *src/main/java*.
- Run *DataMerger*.
- You will be asked to provide a name for the result file - enter one.
- The result file will be created in *src/main/resources/merged_results* directory.

### Data & Licenses
The application contains raw data about postcodes in the UK (England, Scotland & Northern Ireland).

Postcode area & district boundary data has been sourced from GeoLytix ([https://geolytix.co.uk/?geodata](https://geolytix.co.uk/?geodata)) - Postal. The data is released under GeoLytix OpenData and OGL license.

You must always use the following attribution statement to acknowledge the source of the information:
- Postal Boundaries © GeoLytix copyright and database right 2012
- Contains Ordnance Survey data © Crown copyright and database right 2012
- Contains Royal Mail data © Royal Mail copyright and database right 2012
- Contains National Statistics data © Crown copyright and database right 2012