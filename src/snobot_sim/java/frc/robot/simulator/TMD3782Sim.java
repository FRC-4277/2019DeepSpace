package frc.robot.simulator;

import com.snobot.simulator.module_wrapper.ASensorWrapper;
import com.snobot.simulator.module_wrapper.interfaces.II2CWrapper;
import edu.wpi.first.hal.sim.BufferCallback;
import edu.wpi.first.hal.sim.CallbackStore;
import edu.wpi.first.hal.sim.ConstBufferCallback;
import edu.wpi.first.hal.sim.I2CSim;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.Robot;
import frc.robot.commands.JoystickDriveCommand;
import frc.robot.commands.JoystickDriveStopOnLineCommand;
import frc.robot.commands.autonomous.DriveStopOnLineCommand;
import frc.robot.commands.autonomous.groups.LeftCargoshipHatchCommandGroup;
import frc.robot.commands.autonomous.groups.RightCargoshipHatchCommandGroup;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Vector;

public class TMD3782Sim extends ASensorWrapper implements II2CWrapper {
    private final static byte TMD37823_ID = 0x69;
    // Command Flags
    private final static int CMD = 0x80;
    private final static int MULTI_BYTE_BIT = 0x20;
    //
    private final static int REGISTER_BITS   = 0b00011111;
    // Register Addresses
    private final static int ENABLE_REGISTER = 0x00;
    private final static int ID_REGISTER     = 0x12;
    private final static int CDATA_REGISTER  = 0x14;
    private final static int RDATA_REGISTER  = 0x16;
    private final static int GDATA_REGISTER  = 0x18;
    private final static int BDATA_REGISTER  = 0x1A;
    private final static int PDATA_REGISTER  = 0x1C;
    // Enable Register Bitmasks
    private final static int PON   = 0b0000_0001;
    private final static int AEN   = 0b0000_0010;
    private final static int PEN   = 0b0000_0100;

    private final CallbackStore readCallbackStore, writeCallbackStore;
    private boolean poweredOn = false, rgbEnabled = false, proximityEnabled = false;
    private short c = 0, r = 0, b = 0, g = 0, p = 0;
    // Reading
    private int currentRegister = 0;
    private boolean autoIncrement = false;
    // Line Up Mode State
    private boolean lineUpMode = false;
    private long lineUpStartTime = -1;

    TMD3782Sim(int port) {
        super("TMD3782");

        I2CSim wpiWrapper = new I2CSim(port);
        readCallbackStore = wpiWrapper.registerReadCallback(new ReadHandler());
        writeCallbackStore = wpiWrapper.registerWriteCallback(new WriteHandler());
    }

    private class ReadHandler implements BufferCallback {

        @Override
        public void callback(String name, byte[] buffer, int count) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            if (currentRegister == ID_REGISTER) {
                byteBuffer.put(TMD37823_ID);
            } else if (currentRegister <= (BDATA_REGISTER + 0x1)) {
                // We're reading a CRGB byte
                if (!poweredOn || !rgbEnabled) {
                    if (byteBuffer.limit() >= 1) {
                        byteBuffer.put((byte) 0);
                    }
                } else {
                    switch (currentRegister) {
                        // First byte of clear
                        case CDATA_REGISTER:
                            byteBuffer.put((byte)(c & 0xff));
                            break;
                        // Second byte of clear
                        case CDATA_REGISTER + 0x1:
                            byteBuffer.put((byte)((c >> 8) & 0xff));
                            break;

                        // First byte of red
                        case RDATA_REGISTER:
                            byteBuffer.put((byte)(r & 0xff));
                            break;
                        // Second byte of red
                        case RDATA_REGISTER + 0x1:
                            byteBuffer.put((byte)((r >> 8) & 0xff));
                            break;

                        // First byte of green
                        case GDATA_REGISTER:
                            byteBuffer.put((byte)(g & 0xff));
                            break;
                        // Second byte of green
                        case GDATA_REGISTER + 0x1:
                            byteBuffer.put((byte)((g >> 8) & 0xff));
                            break;

                        // First byte of blue
                        case BDATA_REGISTER:
                            byteBuffer.put((byte)(b & 0xff));
                            break;
                        // Second byte of blue
                        case BDATA_REGISTER + 0x1:
                            byteBuffer.put((byte)((b >> 8) & 0xff));
                            break;
                    }
                }
            } else if (currentRegister <= (PDATA_REGISTER + 0x1)){
                if (!poweredOn || !proximityEnabled) {
                    if (byteBuffer.limit() >= 1) {
                        byteBuffer.put((byte) 0);
                    }
                } else {
                    switch (currentRegister) {
                        case PDATA_REGISTER:
                            byteBuffer.put((byte)(p & 0xff));
                            break;
                        case PDATA_REGISTER + 0x1:
                            byteBuffer.put((byte)((p >> 8) & 0xff));
                            break;

                    }
                }
            }

            if (autoIncrement) {
                currentRegister += 0x1;
            }
        }
    }

    private class WriteHandler implements ConstBufferCallback {

        @Override
        public void callback(String name, byte[] buffer, int count) {
            if (buffer.length == 0) {
                return;
            }
            byte command = buffer[0];
            // Check for command bit
            if ((command & CMD) == 0) {
                return;
            }
            // Read next received byte if exists
            byte data = 0;
            if (buffer.length >= 2) {
                data = buffer[1];
            }

            int register = command & REGISTER_BITS;
            // Handler for ENABLE_REGISTER command
            if (register == ENABLE_REGISTER) {
                poweredOn = (data & PON) != 0;
                rgbEnabled = (data & AEN) != 0;
                proximityEnabled = (data & PEN) != 0;
            // Handler for reading
            } else if (buffer.length == 1){
                // We know we're reading for sure
                currentRegister = register;
                autoIncrement = (command & MULTI_BYTE_BIT) != 0;
            }
        }
    }

    // Update clear depending on commands and time
    void update() {
        if (lineUpMode) {
            long elapsedTime = System.currentTimeMillis() - lineUpStartTime;
            if (elapsedTime >= 1000 && elapsedTime <= 1100) {
                System.out.println("LINE UP MODE SET COLORS");
                // 1-1.1s has passed, let's make clear and colors go HIGH
                c = 40;
                r = 5;
                g = 5;
                b = 5;
            }  else if (elapsedTime >= 1200) {
                System.out.println("LINE UP MODE END");
                // 1.2s has passed, let's make clear go low again
                c = 2;
                r = 2;
                g = 2;
                b = 2;
                lineUpMode = false;
            }
        } else {
            if (Robot.mecanumDrive == null) {
                return;
            }
            Command current = Robot.mecanumDrive.getCurrentCommand();

            boolean autoDriveOnLine = false;
            checker: {
                if (current instanceof LeftCargoshipHatchCommandGroup || current instanceof RightCargoshipHatchCommandGroup) {
                    CommandGroup group = (CommandGroup) current;
                    try {
                        Field commandsField = group.getClass().getSuperclass().getDeclaredField("m_commands");
                        commandsField.setAccessible(true);
                        Vector<Object> vector = (Vector<Object>) commandsField.get(group);
                        Field commandField = null;
                        for (Object o : vector) {
                            if (o == null) {
                                continue;
                            }
                            if (commandField == null) {
                                commandField = o.getClass().getDeclaredField("m_command");
                                commandField.setAccessible(true);
                            }
                            Command command = (Command) commandField.get(o);
                            if (command instanceof DriveStopOnLineCommand && command.isRunning()) {
                                autoDriveOnLine = true;
                                break checker;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            if (autoDriveOnLine || current instanceof JoystickDriveStopOnLineCommand) {
                System.out.println("LINE UP MODE START");
                lineUpMode = true;
                lineUpStartTime = System.currentTimeMillis();
                // Start out with low values
                c = 2;
                r = 2;
                g = 2;
                b = 2;
                p = 300;
            }
        }
    }

    @Override
    public void close() throws Exception {
        readCallbackStore.close();
        writeCallbackStore.close();
        super.close();
    }
}
