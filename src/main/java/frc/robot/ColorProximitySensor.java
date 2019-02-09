/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.function.Function;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * Adapted from https://www.chiefdelphi.com/t/writing-code-for-a-color-sensor/167303/8
 */
public class ColorProximitySensor extends SendableBase implements Sendable {
    // IC2 Stuff
    protected final static int I2C_DEVICE_ADDRESS = 0x39;
    protected final static int TMD37821_ID = 0x60;
    protected final static int TMD37823_ID = 0x69;
    // Command Bitmasks
    protected final static int CMD = 0x80; // Most significant bit = 1
    protected final static int MULTI_BYTE_BIT = 0x20;

    // Write Register Addresses
    protected final static int ENABLE_REGISTER  = 0x00;
    protected final static int ATIME_REGISTER   = 0x01;
    protected final static int PPULSE_REGISTER  = 0x0E;

    // Read Register Addresses
    protected final static int ID_REGISTER     = 0x12;
    protected final static int STATUS_REGISTER = 0x13;
    protected final static int CDATA_REGISTER  = 0x14;
    protected final static int RDATA_REGISTER  = 0x16;
    protected final static int GDATA_REGISTER  = 0x18;
    protected final static int BDATA_REGISTER  = 0x1A;
    protected final static int PDATA_REGISTER  = 0x1C;

    // Enable Register Bitmasks (WRITE)
    protected final static int PON   = 0b0000_0001;
    protected final static int AEN   = 0b0000_0010;
    protected final static int PEN   = 0b0000_0100;
    protected final static int WEN   = 0b0000_1000;
    protected final static int AIEN  = 0b0001_0000;
    protected final static int PIEN  = 0b0010_0000;

    // LiveWindow color update MS maximum interval (milliseconds)
    protected final static int LIVE_WINDOW_UPDATE_INTERVAL = 50;

    private final double integrationTime = 10;
    private I2C sensor;
    private volatile ByteBuffer buffer = ByteBuffer.allocate(10);
    private volatile Result lastResult;
    private volatile long lastResultUpdateMs = 0;
    private volatile Status lastStatus;

    // TODO : If needed, allow changing of options of WLONG, AGAIN, ATIME
    public ColorProximitySensor(I2C.Port port) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        sensor = new I2C(port, I2C_DEVICE_ADDRESS); // 0x39 is the IC2 address of the TMD3782

        // Power on, enable RGBC's ADC, Promixity enable
        sensor.write(CMD | 0x00, PON | AEN | PEN);
        
        // Configures the integration time (time for updating color data)
        // Formula can be found in data sheet of TMD3782
        sensor.write(CMD | 0x01, (int) (256 - integrationTime/2.38));        
        
        // TODO : Make this adjustable (1-255)
        sensor.write(CMD | 0x0E, 0b1111); // Set proximity pulse count

        // Read product id
        buffer.clear();
        sensor.read(CMD | ID_REGISTER, 1, buffer);
        byte id = buffer.get();
        if (id != TMD37821_ID && id != TMD37823_ID) {
            DriverStation.reportWarning("Expected product id to be " + hex(TMD37821_ID) + " or " + hex(TMD37823_ID) + " but got " + hex(id), true);
        }

        LiveWindow.add(this);
        setName("Color Proximity Sensor[" + port.name() + "=" + port.value + "]");
    }

    private String hex(int hex) {
        return String.format("0x%02X", hex);
    }

    // TODO : Figure out the point of 0b10000000000000000 if < 0
    public synchronized Result readAll() {
        buffer.clear();
        // MULTI_BYTE_BIT lets us read multiple at the same time. RDATA_REGISTER is the register
        // We're reading from 10 registers, as clear, red, green, blue, and proximity each use two bytes to store a short
        sensor.read(CMD | MULTI_BYTE_BIT | CDATA_REGISTER, 10, buffer);
        
        short clear, red = 0, green = 0, blue = 0, proximity = 0;

        clear = buffer.getShort(0);
        if (clear < 0) { clear += 0b10000000000000000; }

        red = buffer.getShort(2);
        if(red < 0) { red += 0b10000000000000000; }
        
        green = buffer.getShort(4);
        if(green < 0) { green += 0b10000000000000000; }
        
        blue = buffer.getShort(6); 
        if(blue < 0) { blue += 0b10000000000000000; }
        
        proximity = buffer.getShort(8); 
        if(proximity < 0) { proximity += 0b10000000000000000; }
        
        lastResultUpdateMs = System.currentTimeMillis();
        return lastResult = new Result(clear, red, green, blue, proximity);
    }

    public synchronized short read(Register register) {
        buffer.clear();
        
        // Read 2 bytes from register
        sensor.read(CMD | MULTI_BYTE_BIT | register.getRegisterAddress(), 2, buffer);
        
        short result = buffer.getShort(0);
        if (result < 0) { result += 0b10000000000000000; }
        
        return result;
    }

    private short readCached(Register register) {
        Result result;
        if (lastResult == null || (System.currentTimeMillis() - lastResultUpdateMs >= LIVE_WINDOW_UPDATE_INTERVAL)) {
            // Time to update
            result = readAll();
        } else {
            result = lastResult;
        }

        return register.get(result);
    }

    public Result getLastResult() {
        return lastResult;
    }

    public synchronized Status readStatus() {
        buffer.clear();
        sensor.read(CMD | STATUS_REGISTER, 1, buffer);
        return lastStatus = new Status(buffer.get(0));
    }

    public Status getLastStatus() {
        return lastStatus;
    }

    @Override
    public void close() {
        sensor.close();
    }

    public static class Result {
        private short clear = -1, red = -1, green = -1, blue = -1, proximity = -1;

        public Result(short clear, short red, short green, short blue, short proximity) {
            this.clear = clear;
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.proximity = proximity;
        }
        
        public short getClear() {
            return clear;
        }

        public short getRed() {
            return red;
        }        

        public short getGreen() {
            return green;
        }

        public short getBlue() {
            return blue;
        }

        public short getProximity() {
            return proximity;
        }

        /*
        // Maybe implement LUX as a lazy getter?
        // https://github.com/myriots/riots-libraries/blob/master/Riots_TMD3782x/Riots_TMD3782x.cpp
        // https://github.com/myriots/riots-libraries/blob/master/Riots_TMD3782x/Riots_TMD3782x.h
        
        public double getLuxBrightness() {
        }
        
        */
    }

    public static class Status {
        // Status Register Bitmasks
        protected final static int PINT   = 0b00100000;
        protected final static int AINT   = 0b00010000;
        protected final static int PVALID = 0b00000010;
        protected final static int AVALID = 0b00000001;

        // (als stands for Ambient Light Sensor)
        private boolean proximityInterrupt, alsInterrupt, currentProximityInvalid, currentAlsInvalid;

        public Status(int status) {
            proximityInterrupt = (status & PINT) != 0;
            alsInterrupt = (status & AINT) != 0;
            currentProximityInvalid = (status & PVALID) != 0;
            currentAlsInvalid = (status & AVALID) != 0;
        }

        public boolean getProximityInterrupt() {
            return proximityInterrupt;
        }

        public boolean getAlsInterrupt() {
            return alsInterrupt;
        }

        public boolean isCurrentProximityInvalid() {
            return currentProximityInvalid;
        }

        public boolean isCurrentAlsInvalid() {
            return currentAlsInvalid;
        }
    }

    // https://www.chiefdelphi.com/t/creating-custom-smartdashboard-types-like-pidcommand/162737/8
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("ColorProximity");
        builder.addDoubleProperty("Clear", () -> (double) readCached(Register.CLEAR), null); 
        builder.addDoubleProperty("Red", () -> (double) readCached(Register.RED), null);
        builder.addDoubleProperty("Green", () -> (double) readCached(Register.GREEN), null);
        builder.addDoubleProperty("Blue", () -> (double) readCached(Register.BLUE), null);
        builder.addDoubleProperty("Proximity", () -> (double) readCached(Register.PROXIMITY), null);
    }

    public static enum Register {
        CLEAR(CDATA_REGISTER, Result::getClear),
        RED(RDATA_REGISTER, Result::getRed),
        GREEN(GDATA_REGISTER, Result::getGreen),
        BLUE(BDATA_REGISTER, Result::getBlue),
        PROXIMITY(PDATA_REGISTER, Result::getProximity);

        private int registerAddress;
        private Function<Result, Short> resultFunction;

        private Register(int registerAddress, Function<Result, Short> resultFunction) {
            this.registerAddress = registerAddress;
            this.resultFunction = resultFunction;
        }

        public int getRegisterAddress() {
            return registerAddress;
        }

        public short get(Result result) {
            return resultFunction.apply(result);
        }
    }
}
