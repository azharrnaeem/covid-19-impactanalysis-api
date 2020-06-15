# Disclaimer
This is a hands on experience project. Feel free to review and enhance it. 

# covid-19-impactanalysis-api
A simple REST API that can be used to analyze data of globally confirmed cases. It provides multiple endpoints which can be used to fetch data of COVID-19 cases by date, country. Varying cases in the region.

# Configurations
The spring configurations are placed in application.yml file inside resources directory. User name and password are used for authentication whereas for authorization JWT is used. Two tokens are returned in response of login request. One is access token whereas other one is refresh token that can be used to get new token. Upon getting new token previous token is invalidated. JWT token expiry and signature key configurations are available in application.yml

The data source of COID is also configurable as URL. CSV parser is only written for Johns Hopkins' data source. Its URL was given in assignment document.

# Documentation
Documentation Link is as following.Please modify port and host as per environment and configuration
http://localhost:8081/swagger-ui.html#

Special thanks to https://github.com/vkhorikov for some spring security aspects.
