#include <Servo.h>
#include <ESP8266WiFi.h>
#include <FirebaseArduino.h>
#include <Wire.h>
#define FIREBASE_HOST "esp8266-498dc-default-rtdb.firebaseio.com" // Firebase host
#define FIREBASE_AUTH "4KeLpFNXRNdNauXR8yoi59HL8ZiWCLZGYXvT91ce" //Firebase Auth code
#define WIFI_SSID "AndroidAPF4CB" //Enter your wifi Name
#define WIFI_PASSWORD "arduinoUno" // Enter your password
int fireStatus = 0, light, moist;

Servo servo;

void shed(bool on){
  if(on) servo.write(0);
  else servo.write(180);
}

int soilM(){
  return analogRead(A0);
}
void setup() {
    Serial.begin(9600);
//  pinMode(D1, OUTPUT);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.println("Connected.");
  Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  
  int x= D2;
 servo.attach(D4);
 Serial.begin(9600); /* begin serial for debug */
 Wire.begin(D1, D2);
  shed(false);
/* join i2c bus with SDA=D1 and SCL=D2 of NodeMCU */
}

void loop() {
 Wire.beginTransmission(8); /* begin with device address 8 */
 Wire.write("Hello Arduino");  /* sends hello string */
 Wire.endTransmission();    /* stop transmitting */

 Wire.requestFrom(8, 1); /* request & read data of size 13 from slave */
 int x;
 while(Wire.available()){
    char c = Wire.read();
    x = c-'c';
//    Serial.print(c);
 }
 Firebase.set("sunl",x);
 Firebase.set("soil", soilM());
 if(Firebase.getInt("shed_on") == 1){
    shed(true);
 }
 else{
    shed(false);
 }
 if(Firebase.getInt("motor_on") == 1){
    //motor on
    Serial.println("motor_on");
    Firebase.set("motor_on",0);
 }
// Serial.println(soil());
// Serial.println(x);
 delay(2000);
}
