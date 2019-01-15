package org.carbon.crawler.admin.extend.aws.cognito

data class CognitoConfig(
    val region: String,
    val userPoolId: String,
    val clientId: String
)
