package com.emtmm.weatherexample.data;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.emtmm.weatherexample.util.Toolkit;

public class YWeatherDataHandler extends DefaultHandler {
	private static final String YLOC = "location";
    private static final String YWIND = "wind";
    private static final String YATMO = "atmosphere";
    private static final String YASTRO = "astronomy";
    private static final String YCOND = "condition";
    private static final String YFCAST = "forecast";
    
    private int forecastCount;
    private WeatherRecord weatherRecord;

    public YWeatherDataHandler() {
        this.weatherRecord = new WeatherRecord();
    }
    
    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void endDocument() throws SAXException {
    }
    
    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (localName.equals(YWeatherDataHandler.YLOC)) {
            this.weatherRecord.setCity(getAttributeValue("city", atts));
            this.weatherRecord.setRegion(getAttributeValue("region", atts));
            this.weatherRecord.setCountry(getAttributeValue("country", atts));
        }

        if (localName.equals(YWeatherDataHandler.YWIND)) {
            this.weatherRecord.setWindChill(getAttributeValue("chill", atts));
            int windDirectionDegrees = Integer.parseInt(getAttributeValue("direction", atts));
            this.weatherRecord.setWindDirection(Toolkit.convertDirection(windDirectionDegrees));
            this.weatherRecord.setWindSpeed(Integer.parseInt(getAttributeValue("speed", atts)));
        }

        if (localName.equals(YWeatherDataHandler.YATMO)) {
            this.weatherRecord.setHumidity(Double.parseDouble(getAttributeValue("humidity", atts)));
            this.weatherRecord.setVisibility(Double.parseDouble(getAttributeValue("visibility", atts)));
            this.weatherRecord.setPressure(Double.parseDouble(getAttributeValue("pressure", atts)));
            String pressureState = getAttributeValue("rising", atts);
            if (pressureState.equals("0")) {
                this.weatherRecord.setPressureState(WeatherRecord.PRESSURE_STEADY);
            } else if (pressureState.equals("1")) {
                this.weatherRecord.setPressureState(WeatherRecord.PRESSURE_FALLING);
            } else if (pressureState.equals("2")) {
                this.weatherRecord.setPressureState(WeatherRecord.PRESSURE_RISING);
            }
        }

        if (localName.equals(YWeatherDataHandler.YASTRO)) {
            this.weatherRecord.setSunrise(getAttributeValue("sunrise", atts));
            this.weatherRecord.setSunset(getAttributeValue("sunset", atts));
        }

        if (localName.equals(YWeatherDataHandler.YCOND)) {
        	//this.weatherRecord.setText(getAttributeValue("text", atts));
            this.weatherRecord.setTemp(Integer.parseInt(getAttributeValue("temp", atts)));
            int code = Integer.parseInt(getAttributeValue("code", atts));
            WeatherCondition cond = WeatherCondition.getWeatherCondition(code);
            this.weatherRecord.setCondition(cond);
            this.weatherRecord.setDate(getAttributeValue("date", atts));
        }

        if (localName.equals(YWeatherDataHandler.YFCAST)) {
            if (this.forecastCount < 2) {
                WeatherForecast forecast = new WeatherForecast();
                forecast.setDay(getAttributeValue("day", atts));
                forecast.setDate(getAttributeValue("date", atts));
                forecast.setLow(Integer.parseInt(getAttributeValue("low", atts)));
                forecast.setHigh(Integer.parseInt(getAttributeValue("high", atts)));
                int code = Integer.parseInt(getAttributeValue("code", atts));
                WeatherCondition cond = WeatherCondition.getWeatherCondition(code);
                forecast.setCondition(cond);
                this.weatherRecord.getForecasts()[this.forecastCount] = forecast;
            }
            this.forecastCount++;
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
    }

    @Override
    public void characters(char ch[], int start, int length) {
    }
    
    private String getAttributeValue(String attName, Attributes atts) {
        String result = null;
        for (int i = 0; i < atts.getLength(); i++) {
            String thisAtt = atts.getLocalName(i);
            if (attName.equals(thisAtt)) {
                result = atts.getValue(i);
                break;
            }
        }
        return result;
    }
    
    public WeatherRecord getWeatherRecord() {
        return this.weatherRecord;
    }

    public void setWeatherRecord(WeatherRecord weatherRecord) {
        this.weatherRecord = weatherRecord;
    }
}
