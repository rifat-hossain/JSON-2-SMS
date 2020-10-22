# JSON-2-SMS
Send Bulk SMS by using JSON file

# Binary
Coming Soon

# How to use
You can download the binary files and straight install on your phone to start using the app. Or you can clone the repository in your android project and compile by yourself. **But it is recommended to download the binary.**
You will need a JSON file containing all the recipient and sms-body. Sample JSON file is given below
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
