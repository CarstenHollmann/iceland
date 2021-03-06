# 52°North Iceland [![Build Status](https://travis-ci.org/52North/iceland.svg)](https://travis-ci.org/52North/iceland) [![Maven Central](https://img.shields.io/maven-central/v/org.n52.iceland/iceland.svg)](https://search.maven.org/#search|gav|1|g:org.n52.iceland%20AND%20a:iceland)
Iceland is a service framework that enables the development of OGC RPC based services. It features bindings for KVP, POX, SOAP, as well as JSON-based bindings, and facilitates the rapid development of modular services that are easily configured using  [Faroe](https://github.com/52North/faroe). [Svalbard](https://github.com/52North/svalbard) is used for request parsing and response generation.

The 52°North Iceland was previously developed in the the [52°North SOS 4.x](https://github.com/52North/SOS) and was then extracted into an own project to be used in other projects.

## Documentation

Here you can find the [Documentation](doc/Index.md).

## Branches

Ongoing development is done in branch [`develop`](../../tree/develop) and dedicated feature branches (`feature/*`).

## Code Compilation

This project is managed with Maven3. Simply run `mvn clean install`

## Maven dependency

52°North Iceland is provided via Maven Central.

To integrate 52°North Iceland into your Maven project, you can use this dependency definition:

```xml
<dependency>
	<groupId>org.n52.iceland</groupId>
	<artifactId>iceland</artifactId>
	<version>2.0.0</version>
</dependency>
```

## Requirements

Java 8

## Contributing

You are interesting in contributing the 52°North Iceland and you want to pull your changes to the 52N repository to make it available to all?

In that case we need your official permission and for this purpose we have a so called contributors license agreement (CLA) in place. With this agreement you grant us the rights to use and publish your code under an open source license.

A link to the contributors license agreement and further explanations are available [here](http://52north.org/about/licensing/cla-guidelines).



## Support and Contact

You can get support in the [community mailing list and forums](http://52north.org/resources/mailing-lists-and-forums/).

If you encounter any issues with the software or if you would like to see
certain functionality added, let us know at:

 - Carsten Hollmann (c.hollmann@52north.org)
 - Christian Autermann (c.autermann@52north.org)

The Sensor Web Community

52°North Inititative for Geospatial Open Source Software GmbH, Germany

## Credits

The development of the 52°North Iceland implementation was supported by several organizations and projects. We would like to thank the following.

| Project/Logo | Description |
| :-------------: | :------------- |
| <a target="_blank" href="http://www.nexosproject.eu/"><img alt="NeXOS - Next generation, Cost-effective, Compact, Multifunctional Web Enabled Ocean Sensor Systems Empowering Marine, Maritime and Fisheries Management" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_nexos.png" /></a> | The development of this version of the 52&deg;North Iceland was supported by the <a target="_blank" href="http://cordis.europa.eu/fp7/home_en.html">European FP7</a> research project <a target="_blank" href="http://www.nexosproject.eu/">NeXOS</a> (co-funded by the European Commission under the grant agreement n&deg;614102) |
| <a target="_blank" href="https://bmbf.de/"><img alt="BMBF" align="middle"  src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/bmbf_logo_en.png"/></a><a target="_blank" href="https://colabis.de/"><img alt="COLABIS - Collaborative Early Warning Information Systems for Urban Infrastructures" align="middle"  src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/colabis.png"/></a> | The development of this version 52&deg;North Iceland was supported by the <a target="_blank" href="https://www.bmbf.de"> German Federal Ministry of Education and Research</a> research project <a target="_blank" href="https://colabis.de/">COLABIS</a> (co-funded by the German Federal Ministry of Education and Research, programme Geotechnologien, under grant agreement no. 03G0852A) |
| <a target="_blank" href="http://www.odip.org"><img alt="ODIP II - Ocean Data Interoperability Platform" align="middle" width="100" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/odip-logo.png"/></a> | The development of this version of the 52&deg;North Iceland was supported by the <a target="_blank" href="https://ec.europa.eu/programmes/horizon2020/">Horizon 2020</a> research project <a target="_blank" href="http://www.odip.org/">ODIP II</a> (co-funded by the European Commission under the grant agreement n&deg;654310) |
| <a target="_blank" href="http://www.connectingeo.net/"><img alt="ConnectinGEO - Coordinating an Observation Network of Networks EnCompassing saTellite and IN-situ to fill the Gaps in European Observations" align="middle" width="100" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/ConnectinGEO_logo.png"/></a> | The development of this version of the 52&deg;North Iceland was supported by the <a target="_blank" href="https://ec.europa.eu/programmes/horizon2020/">Horizon 2020</a> research project <a target="_blank" href="http://www.connectingeo.net/">ConnectinGEO</a> (co-funded by the European Commission under the grant agreement n&deg;641538) |
| <a target="_blank" href="http://www.geoviqua.org/"><img alt="GeoViQua - QUAlity aware VIsualization for the Global Earth Observation System of Systems" align="middle" width="172" src="https://raw.githubusercontent.com/52North/sos/develop/spring/views/src/main/webapp/static/images/funding/logo_geoviqua.png"/></a> | The development of this version 52&deg;North Iceland was supported by the <a target="_blank" href="http://cordis.europa.eu/fp7/home_en.html">European FP7</a> research project <a href="http://www.geoviqua.org/" title="GeoViQua">GeoViQua</a> (co-funded by the European Commission under the grant agreement n&deg;265178) |
