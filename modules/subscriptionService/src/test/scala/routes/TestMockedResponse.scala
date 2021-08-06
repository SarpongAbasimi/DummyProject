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

}
