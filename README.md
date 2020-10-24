# JSON-2-SMS
Send Bulk SMS by using JSON file on Android

# Releases
[v1.0](https://github.com/rifat-hossain/JSON-2-SMS/releases/tag/1.0)

# How to use
Download binary (\*.apk) from one of the releases showed above and install on your mobile device. Then create a JSON file containing all the information about the recipients and text-bodies to send as sms. An example of the required json file struckture is given below-
```
{
  "SMSs":
  [
      {
          "To":"+8801231231231",
          "Body":"Sms Body"
      },
      {
          "To":"01231231232",
          "Body":"Sms Body 2"
      }
  ]
}
```
What you all need to do is open the app and select the required JSON file and press *SEND TO ALL* button to send all the messages to recipients
# Screenshot
![Screenshot](https://rifat-hossain.github.io/images/json2sms.png)
