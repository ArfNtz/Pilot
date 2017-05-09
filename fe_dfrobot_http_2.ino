#include <UnoWiFiDevEd.h>

int S1 = 6; //Motor1 Speed Control
int S2 = 5; //Motor2 Speed Control
int D1 = 8; //Motor1 Direction Control
int D2 = 7; //Motor2 Direction Control

void setup() {
  int i;
  for (i = 5; i <= 8; i++) pinMode(i, OUTPUT);
  Wifi.begin();
}

void loop() {
  while(Wifi.available()) { process(Wifi); }
  delay(50);
}

void process(WifiData wifi) {
  String _cmd = wifi.readStringUntil('/');
  if (_cmd == "digital") { cmd(wifi); }
}

// example : http://192.168.1.67/arduino/digital/255/255/1/1
void cmd(WifiData wifi) {
  // Parse
  int s1,s2,d1,d2;
  s1 = wifi.parseInt();
  if (wifi.read() == '/') {
    s2 = wifi.parseInt();
    if (wifi.read() == '/') {
     d1 = wifi.parseInt();
     if (wifi.read() == '/') {
       d2 = wifi.parseInt();
      }
    }
  }
  // debug print
  wifi.println("HTTP/1.1 200 OK\n");
  wifi.print(F("s1=")); wifi.println(s1);
  wifi.print(F("s2=")); wifi.println(s2);
  wifi.print(F("d1=")); wifi.println(d1);
  wifi.print(F("d2=")); wifi.println(d2);
  // Limit range
  if(s1<0)s1=0; if(s1>255)s1=255;
  if(s2<0)s2=0; if(s2>255)s2=255;
  if(s1==0)s1=LOW;if(s2==0)s2=LOW;
  if(d1<0)d1=LOW; if(d1>0)d1=HIGH;
  if(d2<0)d2=LOW; if(d2>0)d2=HIGH;
  // Move
  if (s1==LOW) digitalWrite(S1,s1); else analogWrite(S1,s1);
  if (s2==LOW) digitalWrite(S2,s2); else analogWrite(S2,s2);
  digitalWrite(D1,d1);
  digitalWrite(D2,d2);
  // End
  wifi.print(EOL);
}

