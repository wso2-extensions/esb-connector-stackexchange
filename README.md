# StackExchange EI Connector

The StackExchange [Connector](https://docs.wso2.com/display/EI640/Working+with+Connectors) allows you to access the [StackExchange REST API](https://api.stackexchange.com/docs) 
through WSO2 Enterprise Integrator (WSO2 EI). StackExchange is a network of question-and-answer websites on topics in 
diverse fields, each site covering a specific topic, where questions, answers, and users are subject to a reputation 
award process. The reputation system allows the sites to be self-moderating.

## Compatibility

| Connector version | StackExchange API version | Supported WSO2 EI version |
| ------------- | ------------- | ------------- |
| [1.0.0](https://github.com/wso2-extensions/esb-connector-stackexchange/tree/org.wso2.carbon.connector.stackexchange-1.0.0) | 2.2 | EI 6.4.0 |

## Getting started

###### Download and install the connector

1. Download the connector from [WSO2 Store](https://store.wso2.com/store/assets/esbconnector/details/511c00f9-8529-4e50-b058-032deecdc802) by clicking the Download Connector button.
2. Then you can follow this [Documentation](https://docs.wso2.com/display/EI640/Working+with+Connectors+via+the+Management+Console) to add and enable the connector via the Management Console in your EI instance.
3. For more information on using connectors and their operations in your EI configurations, see [Using a Connector](https://docs.wso2.com/display/EI640/Using+a+Connector).
4. If you want to work with connectors via EI tooling, see [Working with Connectors via Tooling](https://docs.wso2.com/display/EI640/Working+with+Connectors+via+Tooling).

###### Configuring the connector operations

To get started with google spreadsheet connector and their operations, see [Configuring StackExchnge Operations](docs/config.md).

## Building from the Source

Follow the steps given below to build the StackExchange connector from the source code:

1. Get a clone or download the source from [Github](https://github.com/wso2-extensions/esb-connector-stackexchange).
2. Run the following Maven command from the `esb-connector-stackexchange` directory: `mvn clean install`.
3. The `stackexchange-connector-X.X.X.zip` file is created in the `esb-connector-stackexchange/target` directory

## How you can contribute

As an open source project, WSO2 extensions welcome contributions from the community.
Check the [issue tracker](https://github.com/wso2-extensions/esb-connector-stackexchange/issues) for open issues that interest you. We look forward to receiving your contributions.
