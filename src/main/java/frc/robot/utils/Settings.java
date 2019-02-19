/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.utils;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTableValue;
import edu.wpi.first.networktables.TableEntryListener;
import edu.wpi.first.wpilibj.shuffleboard.*;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A utilty class to allow easy creation of Settings in the "Settings" tab in Shuffleboard using builder style.
 *
 * <p> Examples:
 * <pre>
 * Setting<String> robotNameSetting = Settings.createTextField("Robot Name", true).build();
 *
 * Setting<Integer> colorThreshold = Settings.createIntField("Color Threshold", true).build();
 *
 * // Double slider
 * Setting<Double> motorOutputLimit = Settings
 *  .createDoubleSlider("Motor Output Limit", true)
 *  .range(0.0, 1.0)
 *  .increment(0.1)
 *  .build();
 *
 * // Booean toggle switch
 * Setting<Boolean> invertDrive = Settings
 *  .createToggleSwitch("Invert Drive", true)
 *  .defaultValue(true)
 *  .build();
 *
 * // Normal selector
 * ChooserSetting<Integer> armStartingPosition = Settings
 *  .createChooser("Arm Starting Position")
 *  .addDefault("UP", 0)
 *  .addOption("DOWN", 180)
 *  .build();
 *
 * // Enum selector
 * ChooserSetting<RoundingMode> roundingModeSetting = Settings
 *  .createEnumChooser(RoundingMode.class, "Rounding Mode")
 *  // Default value is specified here, while adding all enum values
 *  .addAll(RoundingMode.HALF_DOWN)
 *  .build();
 * </pre>
 */
public class Settings {

    public static TextFieldBuilder createTextField(String name, boolean persistent) {
        return new TextFieldBuilder(name, persistent);
    }

    public static NumberFieldBuilder<Integer> createIntField(String name, boolean persistent) {
        return new NumberFieldBuilder<>(name, persistent, entry -> toInteger(entry.getDouble(0.0)), 0);
    }

    public static NumberFieldBuilder<Double> createDoubleField(String name, boolean persistent) {
        return new NumberFieldBuilder<>(name, persistent, entry -> entry.getDouble(0.0), 0.0);
    }

    public static SliderBuilder<Integer> createIntSlider(String name, boolean persistent) {
        return new SliderBuilder<>(name, persistent, entry -> toInteger(entry.getDouble(0.0)), 0);
    }

    public static SliderBuilder<Double> createDoubleSlider(String name, boolean persistent) {
        return new SliderBuilder<>(name, persistent, entry -> entry.getDouble(0.0), 0.0);
    }

    public static ToggleButtonBuilder createToggleButton(String name, boolean persistent) {
        return new ToggleButtonBuilder(name, persistent);
    }

    public static ToggleSwitchBuilder createToggleSwitch(String name, boolean persistent) {
        return new ToggleSwitchBuilder(name, persistent);
    }

    public static <T> ChooserBuilder<T> createChooser(String name) {
        return new ChooserBuilder<T>(name);
    }

    public static <T extends Enum<T>> EnumChooserBuilder<T> createEnumChooser(Class<T> enumClass, String name) {
        return new EnumChooserBuilder<T>(enumClass, name);
    }

    public static class TextFieldBuilder extends EntrySettingBuilder<String> {
        TextFieldBuilder(String name, boolean persistent) {
            super(name, persistent, BuiltInWidgets.kTextView, entry -> entry.getString(""), "");
        }
    }

    public static class NumberFieldBuilder<T extends Number> extends EntrySettingBuilder<T> {
        NumberFieldBuilder(String name, boolean persistent, Function<NetworkTableEntry, T> grabber, T defaultValue) {
            super(name, persistent, BuiltInWidgets.kTextView, grabber, defaultValue);
        }

        @Override
        public NumberFieldBuilder<T> defaultValue(T defaultValue) {
            super.defaultValue(defaultValue);
            return this;
        }
    }

    public static class SliderBuilder<T extends Number> extends EntrySettingBuilder<T> {
        SliderBuilder(String name, boolean persistent, Function<NetworkTableEntry, T> grabber, T defaultValue) {
            super(name, persistent, BuiltInWidgets.kNumberSlider, grabber, defaultValue);
        }

        public SliderBuilder<T> range(Number min, Number max) {
            min(min);
            max(max);
            return this;
        }

        public SliderBuilder<T> min(Number min) {
            setting.setProperty("Min", min);
            return this;
        }

        public SliderBuilder<T> max(Number max) {
            setting.setProperty("Max", max);
            return this;
        }

        public SliderBuilder<T> increment(Number increment) {
            setting.setProperty("Block increment", increment);
            return this;
        }

        @Override
        public SliderBuilder<T> defaultValue(T defaultValue) {
            super.defaultValue(defaultValue);
            return this;
        }
    }

    public static class ToggleButtonBuilder extends ToggleBuilder {
        ToggleButtonBuilder(String name, boolean persistent) {
            super(name, BuiltInWidgets.kToggleButton, persistent);
        }
    }

    public static class ToggleSwitchBuilder extends ToggleBuilder {
        ToggleSwitchBuilder(String name, boolean persistent) {
            super(name, BuiltInWidgets.kToggleSwitch, persistent);
        }
    }

    public static class ToggleBuilder extends EntrySettingBuilder<Boolean> {
        ToggleBuilder(String name, WidgetType type, boolean persistent) {
            super(name, persistent, type, entry -> entry.getBoolean(false), false);
        }
    }

    public static class EnumChooserBuilder<T extends Enum<T>> extends ChooserBuilder<T> {
        private Class<T> enumClass;

        EnumChooserBuilder(Class<T> enumClass, String name) {
            super(name);
            this.enumClass = enumClass;
        }

        public ChooserSetting<T> build() {
            super.build();
            return (ChooserSetting<T>) setting;
        }

        public EnumChooserBuilder<T> addDefault(T value) {
            addDefault(value.name(), value);
            return this;
        }

        public EnumChooserBuilder<T> addOption(T value) {
            addOption(value.name(), value);
            return this;
        }

        public EnumChooserBuilder<T> addAll(T defaultValue) {
            addDefault(defaultValue);
            for (T enumValue : enumClass.getEnumConstants()) {
                if (enumValue != defaultValue) {
                    addOption(enumValue);
                }
            }
            return this;
        }

        public EnumChooserBuilder<T> position(int x, int y) {
            super.position(x, y);
            return this;
        }

        public EnumChooserBuilder<T> size(int x, int y) {
            super.size(x, y);
            return this;
        }

        public EnumChooserBuilder<T> autoPrintValueChange(Function<T, String> stringifier) {
            super.autoPrintValueChange(stringifier);
            return this;
        }

        public EnumChooserBuilder<T> autoPrintValueChange() {
            super.autoPrintValueChange();
            return this;
        }
    }

    public static class ChooserBuilder<T> extends SettingBuilder<T> {
        ChooserBuilder(String name) {
            setting = new ChooserSetting<T>(name, BuiltInWidgets.kComboBoxChooser);
        }

        public ChooserSetting<T> build() {
            super.build();
            return (ChooserSetting<T>) setting;
        }

        public ChooserBuilder<T> addDefault(String name, T value) {
            ((ChooserSetting<T>) setting).addDefault(name, value);
            return this;
        }

        public ChooserBuilder<T> addOption(String name, T value) {
            ((ChooserSetting<T>) setting).addOption(name, value);
            return this;
        }

        public ChooserBuilder<T> position(int x, int y) {
            super.position(x, y);
            return this;
        }

        public ChooserBuilder<T> size(int x, int y) {
            super.size(x, y);
            return this;
        }

        public ChooserBuilder<T> autoPrintValueChange(Function<T, String> stringifier) {
            super.autoPrintValueChange(stringifier);
            return this;
        }

        public ChooserBuilder<T> autoPrintValueChange() {
            super.autoPrintValueChange();
            return this;
        }
    }

    public static class EntrySettingBuilder<T> extends SettingBuilder<T> {
        EntrySettingBuilder(String name, boolean persistent, WidgetType widgetType,
                            Function<NetworkTableEntry, T> grabber, T defaultValue) {
            setting = new EntrySetting<>(name, persistent, widgetType, grabber, defaultValue);
        }

        public EntrySettingBuilder<T> defaultValue(T defaultValue) {
            ((EntrySetting<T>) setting).defaultValue = defaultValue;
            return this;
        }

        public Setting<T> build() {
            super.build();
            return (EntrySetting<T>) setting;
        }

        public EntrySettingBuilder<T> position(int x, int y) {
            super.position(x, y);
            return this;
        }

        public EntrySettingBuilder<T> size(int x, int y) {
            super.size(x, y);
            return this;
        }

        public EntrySettingBuilder<T> autoPrintValueChange(Function<T, String> stringifier) {
            super.autoPrintValueChange(stringifier);
            return this;
        }

        public EntrySettingBuilder<T> autoPrintValueChange() {
            super.autoPrintValueChange();
            return this;
        }
    }

    public static class SettingBuilder<T> {
        Setting<T> setting;
        private boolean built = false;

        public SettingBuilder<T> position(int x, int y) {
            setting.setPosition(x, y);
            return this;
        }

        public SettingBuilder<T> size(int width, int height) {
            setting.setSize(width, height);
            return this;
        }

        public SettingBuilder<T> autoPrintValueChange(Function<T, String> stringifier) {
            setting.addUpdateListener(value -> System.out.println(stringifier.apply(value)));
            return this;
        }

        public SettingBuilder<T> autoPrintValueChange() {
            autoPrintValueChange(value -> "Setting '" + setting.name + "'" + " changed to " + Objects.toString(value));
            return this;
        }

        public Setting<T> build() {
            if (built) {
                throw new IllegalStateException("Setting already built");
            }
            setting.create();
            return setting;
        }
    }

    public static abstract class Setting<T> {
        String name;

        abstract void create();
        abstract void setProperty(String name, Object value);
        abstract void setPosition(int x, int y);
        abstract void setSize(int x, int y);
        public abstract T getValue();
        public abstract void setValue(T value);
        public abstract void addUpdateListener(Consumer<T> listener);
    }

    public static class ChooserSetting<T> extends Setting<T> {
        // OPTIONS before creation
        private WidgetType widgetType;
        private Integer positionX, positionY;
        private Integer sizeX, sizeY;
        private SendableChooser<T> chooser = new SendableChooser<>();
        private T defaultValue;
        private Map<String, T> options = new HashMap<>();
        private Map<String, Object> widgetProperties = new HashMap<>();
        private List<Consumer<T>> entryListeners = new ArrayList<>();
        // After creation
        private ComplexWidget widget;
        private NetworkTable table;

        /**
         * A setting in the ShuffleBoard "Settings" tab
         * @param name Display name
         * @param widgetType Type of widget
         */
        private ChooserSetting(String name, WidgetType widgetType) {
            this.name = name;
            this.widgetType = widgetType;
        }

        @Override
        void create() {
            ShuffleboardTab tab = Shuffleboard.getTab("Settings");
            widget = tab.add(name, chooser);
            widget.withWidget(widgetType);
            widget.withProperties(widgetProperties);
            if (positionX != null && positionY != null) {
                widget.withPosition(positionX, positionY);
            }
            if (sizeX != null && sizeY != null) {
                widget.withSize(sizeX, sizeY);
            }
            table = NetworkTableInstance.getDefault()
                    .getTable("Shuffleboard")
                    .getSubTable("Settings")
                    .getSubTable(name);
            table.addEntryListener("selected", new TableEntryListener(){
                @Override
                public void valueChanged(NetworkTable table, String key, NetworkTableEntry entry, NetworkTableValue value,
                                         int flags) {
                    entryListeners.forEach(listener -> listener.accept(options.get(value.getValue())));
                }
            }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kLocal);

            if (defaultValue != null) {
                setValue(defaultValue);
            }
        }

        @Override
        void setProperty(String name, Object value) {
            widgetProperties.put(name, value);
        }

        @Override
        void setPosition(int x, int y) {
            this.positionX = x;
            this.positionY = y;
        }

        @Override
        void setSize(int x, int y) {
            this.sizeX = x;
            this.sizeY = y;
        }

        @Override
        public T getValue() {
            return chooser.getSelected();
        }

        @Override
        public void setValue(T value) {
            for (Map.Entry<String, T> entry : options.entrySet()) {
                if (Objects.equals(entry.getValue(), value)) {
                    setSelected(entry.getKey());
                    return;
                }
            }
        }

        public void setSelected(String key) {
            table.getEntry("selected").setValue(key);
        }

        @Override
        public void addUpdateListener(Consumer<T> listener) {
            entryListeners.add(listener);
        }

        void addDefault(String name, T value) {
            defaultValue = value;
            chooser.setDefaultOption(name, value);
            options.put(name, value);
        }

        void addOption(String name, T value) {
            chooser.addOption(name, value);
            options.put(name, value);
        }

        public T getSelected() {
            return chooser.getSelected();
        }
    }

    public static class EntrySetting<T> extends Setting<T> {
        // OPTIONS before creation
        T defaultValue;
        boolean persistent;
        private WidgetType widgetType;
        private Function<NetworkTableEntry, T> grabber;
        private Integer positionX, positionY;
        private Integer sizeX, sizeY;
        private Map<String, Object> widgetProperties = new HashMap<>();
        private List<Consumer<T>> entryListeners = new ArrayList<>();
        // After creation
        private SimpleWidget widget;
        private NetworkTableEntry entry;

        /**
         * A setting in the ShuffleBoard "Settings" tab
         * @param name Display name
         * @param persistent Whether setting value is saved to RoboRIO
         * @param widgetType Type of widget
         * @param grabber Function to grab value from network table entry
         * @param defaultValue Default value
         */
        private EntrySetting(String name, boolean persistent, WidgetType widgetType,
                             Function<NetworkTableEntry, T> grabber, T defaultValue) {
            this.name = name;
            this.persistent = persistent;
            this.widgetType = widgetType;
            this.grabber = grabber;
            this.defaultValue = defaultValue;
        }

        @Override
        void create() {
            ShuffleboardTab tab = Shuffleboard.getTab("Settings");
            widget = persistent ? tab.addPersistent(name, defaultValue) : tab.add(name, defaultValue);
            widget.withWidget(widgetType);
            widget.withProperties(widgetProperties);
            if (positionX != null && positionY != null) {
                widget.withPosition(positionX, positionY);
            }
            if (sizeX != null && sizeY != null) {
                widget.withSize(sizeX, sizeY);
            }
            entry = widget.getEntry();
            entry.addListener(entryNotification ->
                            entryListeners.forEach(
                                    listener -> listener.accept(grabber.apply(entryNotification.getEntry()))),
                    EntryListenerFlags.kNew | EntryListenerFlags.kUpdate | EntryListenerFlags.kLocal
            );
        }

        @Override
        void setProperty(String name, Object value) {
            widgetProperties.put(name, value);
        }

        @Override
        void setPosition(int x, int y) {
            this.positionX = x;
            this.positionY = y;
        }

        @Override
        void setSize(int x, int y) {
            this.sizeX = x;
            this.sizeY = y;
        }

        @Override
        public T getValue() {
            return grabber.apply(entry);
        }

        @Override
        public void setValue(T value) {
            entry.setValue(value);
        }

        @Override
        public void addUpdateListener(Consumer<T> listener) {
            entryListeners.add(listener);
        }
    }

    private static int toInteger(double doubleValue) {
        return Double.valueOf(doubleValue).intValue();
    }
}
