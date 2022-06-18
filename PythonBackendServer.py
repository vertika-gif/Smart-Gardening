import time
from bs4 import BeautifulSoup
import requests
headers = {
	'User-Agent':
		'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3'}


def weather(city):
	city = city.replace(" ", "+")
	res = requests.get(
		f'https://www.google.com/search?q={city}&oq={city}&aqs=chrome.0.35i39l2j0l4j46j69i60.6128j1j7&sourceid=chrome&ie=UTF-8', headers=headers)
	weather = BeautifulSoup(res.text, 'html.parser').select('#wob_tm')[0].getText().strip()
	return weather


def humidity(city):
  city = city.replace(" ", "+")
  res = requests.get(
      f'https://www.google.com/search?q={city}&oq={city}&aqs=chrome.0.35i39l2j0l4j46j69i60.6128j1j7&sourceid=chrome&ie=UTF-8', headers=headers)
  weather = BeautifulSoup(res.text, 'html.parser').select('#wob_hm')[0].getText().strip()
  return weather  

#returns rainfall info
def precipitation(city):
  city = city.replace(" ", "+")
  res = requests.get(
      f'https://www.google.com/search?q={city}&oq={city}&aqs=chrome.0.35i39l2j0l4j46j69i60.6128j1j7&sourceid=chrome&ie=UTF-8', headers=headers)
  weather = BeautifulSoup(res.text, 'html.parser').select('#wob_pp')[0].getText().strip()
  return weather  


humidityVal = humidity("Delhi humidity")
n=len(humidityVal)
humidityVal=humidityVal[:n-1]
humidityVal=int(humidityVal)

precipitationVal = precipitation("Delhi precipitation")
n=len(precipitationVal)
precipitationVal=precipitationVal[:n-1]
precipitationVal=int(precipitationVal)

temperatureVal = weather("Delhi weather")
temperatureVal=int(temperatureVal)

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

cred = credentials.Certificate('a.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': "https://esp8266-498dc-default-rtdb.firebaseio.com"
})

soilMoisureSensor = db.reference('soil')
soilMoisureSensorVal =soilMoisureSensor.get()

humidity = db.reference('humd')
humidity.set(humidityVal)

shed=db.reference('shed')
isShedOn=db.reference('shed_on')

motor = db.reference('motor_on')
isMotorOn = motor.get()

temp = db.reference('temp')
tempVal= temp.get()

sunlight = db.reference('sunl')
sunlightVal = sunlight.get()

waterTankEmpty = db.reference('waterTankEmpty')
isTankEmpty = waterTankEmpty.get()

lastWater = db.reference('watr')
lastWaterTime = lastWater.get()

moistureThresholdVal=480

def pumpWater(soilMoisureSensorVal):
  # check water tank empty
  motor.set(1)
  if(humidityVal>90 and weather<20):
    while(1):  
      time.sleep(1)
      soilMoisureSensorVal=soilMoisureSensor.get()
      if(soilMoisureSensorVal<(moistureThresholdVal*3/2)):
            return    
  else:
    while(1):
        soilMoisureSensorVal=soilMoisureSensor.get()
        if(soilMoisureSensorVal<moistureThresholdVal): 
          # motor.set(0)
          return


def helper(soilMoisureSensorVal):
  pumpWater(soilMoisureSensorVal)
  initial()

def initial():
  while(1):
      time.sleep(1)
      soilMoisureSensorVal=soilMoisureSensor.get()
      sunlightVal = sunlight.get()
      if(soilMoisureSensorVal>moistureThresholdVal):
        helper(soilMoisureSensorVal)
        break
      if(sunlightVal>=6):
        shed.set(1)
        isShedOn.set(1)
      else:
        shed.set(0)
        isShedOn.set(0)

initial()
      

