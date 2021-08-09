package routes

import io.circe.Json
import io.circe.literal.JsonStringContext

object TestMockedResponse {
  val mockedGetUserSubscriptionResponse: Json = {
    json"""
        {
          "subscriptions":[
            {
              "organization":"47deg",
              "repository":"thool",
              "subscribedAt":"2021-04-15T12:30:15"
            },
            {
              "organization":"higherkindness",
              "repository":"skeuomorph",
              "subscribedAt":"2021-04-26T15:45:25"
            },
            {
              "organization":"47degrees",
              "repository":"github4s",
              "subscribedAt":"2021-05-01T09:15:05"
            }
          ]
        }
        """

  }

  val mockedPostUserSubscriptionResponse: Json = {
    json"""
        {
          "subscriptions":[
            {
              "organization":"47deg",
              "repository":"thool"
            }
          ]
        }
        """
  }

  def mockedSlackCommandResponse: Json = {
    json"""
      {
        "response_type":"in_channel",
        "text":"It's 80 degrees right now.",
        "blocks":[
        {
          "type":"section",
          "text":{
            "type":"mrkdwn",
            "text":"*It's 80 degrees right now.*"
          }
        },
        {
          "type":"section",
          "text":{
            "type":"mrkdwn",
            "text":"Partly cloudy today and tomorrow"
          }
        }
        ]
      }
    """
  }

  def mockedSlackCommandBody: Json = {
    json"""
    {
      "token" : "token",
      "team_id" : "teamId",
      "team_domain" : "teamDomain",
      "enterprise_id" : "enterpriseId",
      "enterprise_name" : "enterpriseName",
      "channel_id" : "channelId",
      "channel_name" : "channelName",
      "user_id" : "userId",
      "user_name" : "userName",
      "command" : "command",
      "text" : "text",
      "response_url" : "responseUrl",
      "trigger_id" : "triggerId",
      "api_app_id" : "apiAppId"
    }
    """
  }
}
