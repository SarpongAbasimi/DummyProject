package mockedSubscriptionResponse
import io.circe.Json
import io.circe.literal._

object MockedResponse {
  def mockedGetUserSubscriptionResponse: Json = {
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

  def mockedPostUserSubscriptionResponse: Json = {
    json"""
        {
          "subscriptions":[
            {
              "organization":"47deg",
              "repository":"thool"
            },
            {
              "organization":"higherkindness",
              "repository":"skeuomorph"
            },
            {
              "organization":"47degrees",
              "repository":"github4s"
            }
          ]
        }
        """
  }
}
