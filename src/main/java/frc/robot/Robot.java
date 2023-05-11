// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Compressor;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
//limelight imports ADD THIS TO MASTER CODE
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  //Initializing the controller
  Joystick logicController = new Joystick(0);
  Joystick logicEXController = new Joystick(1);

  //Initializing motors
  Spark leftFront = new Spark(0); 
  Spark leftBack = new Spark(1);
  Spark rightFront = new Spark(2);
  Spark rightBack = new Spark(3);

  Spark intake1 = new Spark(4);
  Spark intake2 = new Spark(8);
  Spark elevator = new Spark(5);
  Spark climber1 = new Spark(6);
  Spark climber2 = new Spark(7);
  Spark shaft = new Spark(9);

  TalonFX shooter = new TalonFX(1);
  TalonFX turret = new TalonFX(2);

  Boolean toggleTurret = false;
  Boolean toggleClimbers = false;

  Compressor pcmCompressor = new Compressor(0, PneumaticsModuleType.CTREPCM);
  
  DoubleSolenoid exampleDoublePCM = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1);
  DoubleSolenoid exampleDoublePH = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);

  

  //Initializing motor groups
  MotorControllerGroup leftMotors = new MotorControllerGroup(leftFront, leftBack);
  MotorControllerGroup rightMotors = new MotorControllerGroup(rightFront, rightBack);

  //Initializing the drivetrain
  DifferentialDrive driveTrainMotors = new DifferentialDrive(leftMotors, rightMotors);
  double firstParem = 0.0;
  double secondParem = 0.0;


  //Initializing the timer for autonomous/climber
  Timer autoTimer = new Timer();
  Timer climberTimer = new Timer();


  NetworkTableEntry tx;
  NetworkTableEntry ta;
  NetworkTableEntry ty;

  


  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
    leftFront.setInverted(true);
    
    leftBack.setInverted(true);
    m_robotContainer = new RobotContainer();
    //pcmCompressor.enableDigital();
    //pcmCompressor.disable();


        //LIMELIGHT - anything with LIMELIGHT add to master code
        NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");
        tx = table.getEntry("tx");
        ty = table.getEntry("ty");
        ta = table.getEntry("ta");
    
  }
  boolean enabled = pcmCompressor.isEnabled();
  
  boolean pressureSwitch = pcmCompressor.getPressureSwitchValue();
  //enabled == false;
  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /** This function is called once each time the robot enters Disabled mode. */
  @Override
  public void disabledInit() {}

  @Override
  public void disabledPeriodic() {}

  /** This autonomous runs the autonomous command selected by your {@link RobotContainer} class. */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
    autoTimer.reset();
    autoTimer.start();
    
    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {

    if (autoTimer.get() < 1.7) {
      driveTrainMotors.tankDrive(0.75, 0.75);
      shooter.set(ControlMode.PercentOutput,-0.63);
      // less than 2 feet
    }
    else if (autoTimer.get() >= 2) {
      shooter.set(ControlMode.PercentOutput, -0.63);
      driveTrainMotors.tankDrive(0, 0);
      elevator.set(-0.5);
    }
    else {
      driveTrainMotors.tankDrive(0.0, 0.0);
     // shooter.set(ControlMode.PercentOutput,-0.7);
     // elevator.set(-0.5);
    }
    /*else if ((autoTimer.get() > 0.5) && (autoTimer.get() < 2.3)){
      driveTrainMotors.tankDrive(-0.75, -0.75);
      //intake1.set(0.75);
      //intake2.set(0.5);
      // make sure it goes 10+ feet
    }
    else if  ((autoTimer.get() > 2.3) && (autoTimer.get() < 4.3)){
      driveTrainMotors.stopMotor();
    }   
    else if  ((autoTimer.get() > 4.3) && (autoTimer.get() < 5.5)){
      driveTrainMotors.tankDrive(0.7, -0.7);
      //shooter.set(ControlMode.PercentOutput, 1.0);
      //elevator.set(0.5);
    }*/
    
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    //CameraServer.startAutomaticCapture();
    // UsbCamera camera1 = CameraServer.startAutomaticCapture();
    // UsbCamera camera2 = CameraServer.startAutomaticCapture();
  }
  
  
  
  
  
      
  
  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    
    //Allows the drivetrain to drive
    //firstParem = Math.pow(logicController.getRawAxis(1), 3); //Trying to slow down the robot
    //secondParem = Math.pow(logicController.getRawAxis(5), 3); //Trying to slow down the robot
    //driveTrainMotors.tankDrive(firstParem, secondParem);
    driveTrainMotors.tankDrive(0.89 * logicController.getRawAxis(1), 0.89 * logicController.getRawAxis(5));
    //int timer; //Change the speed of creature
    //timer = 0;
    //timer++;
    
 
    //LIMELIGHT read values periodically
    double x = tx.getDouble(0.0);
    double y = ty.getDouble(0.0);
    double area = ta.getDouble(0.0);
    int turnLimit = (int) turret.getSelectedSensorPosition();
   
    


    boolean joystickTurn = true;
    if (logicEXController.getRawButton(5)){
      joystickTurn = false;
      

    }
    
    else{
      joystickTurn = true;
    }

    /*if (x != 0.0 && y != 0.0){
      SmartDashboard.putBoolean("Target Found", true);
    }*/
    
    if (joystickTurn == true && x <= -10.0 && x >= -30.0 && turnLimit >= -100000){
      turret.set(ControlMode.PercentOutput, -0.60);
    
    }
    else if (joystickTurn == true && x >= 10.0 && x <= 30.0 && turnLimit <= 150000){
      turret.set(ControlMode.PercentOutput, 0.60);
    
    }
    else if (joystickTurn == false &&  turnLimit >= -100000 && logicEXController.getRawAxis(0) == -1){
      turret.set(ControlMode.PercentOutput, -0.60);
    }
    else if (joystickTurn == false &&  turnLimit <= 150000 && logicEXController.getRawAxis(0) == 1){
      turret.set(ControlMode.PercentOutput, 0.60);
    }
    else if (logicEXController.getRawButton(8) && turnLimit > 100){
      turret.set(ControlMode.PercentOutput, -0.60);
     
    }
    else if (logicEXController.getRawButton(8) && turnLimit < -100){
      turret.set(ControlMode.PercentOutput, 0.60);
    }
    else {

      SmartDashboard.putBoolean("Target Found", false);
      turret.set(ControlMode.PercentOutput, 0);
    }
    

    /*if (LogicEXController.getRawButton(2) && x <= -10.0 && x >= -30.0 && turnLimit >= -100000){
      turret.set(ControlMode.PercentOutput, -0.60);
    
    }
    else if (LogicEXController.getRawButton(2) && x >= 10.0 && x <= 30.0 && turnLimit <= 150000){
      turret.set(ControlMode.PercentOutput, 0.60);
    
    }

    else {
      SmartDashboard.putBoolean("Target Found", false);
      turret.set(ControlMode.PercentOutput, 0);
     
    
    }*/
    if (y >= 4 && logicController.getRawButton(2) ){
      leftMotors.set(0.7);
      rightMotors.set(0.7);
    }
    else if (y <= 4 && logicController.getRawButton(2) ){
      leftMotors.set(-0.65);
      rightMotors.set(-0.65);
    }
    else{
      driveTrainMotors.tankDrive(0.78 * logicController.getRawAxis(1), 0.7 * logicController.getRawAxis(5));
    }
    
    //LIMELIGHT post to smart dashboard periodically
    SmartDashboard.putNumber("LimelightX", x);
    SmartDashboard.putNumber("LimelightY", y);
    SmartDashboard.putNumber("LimelightArea", area);
    
    //Buttons
    
    if (logicController.getRawButton(1)){
      //climber1.set(-0.5);
      //climber2.set(-0.5);
      toggleClimbers = false;
      climberTimer.reset();
      climber1.set(-0.65);
    }
    else if (logicController.getRawButton(4)){
      toggleClimbers = true;
      climber1.set(0.65);
      //timer = 0;
      //climberTimer.reset();
      //climberTimer.start();
    }
    else if (logicController.getRawButton(8)){
      climber1.set(0.5);
    }
    else {
      climber1.set(0);
    }

   /* if (toggleClimbers == true & climberTimer.get() <= 1) {
      climber1.set(-0.5);
      //climber2.set(0.5);
    }
    else if (climberTimer.get() == 0) {
      climber1.set(0.0);
      //climber2.set(0.0);
    }
    else if (toggleClimbers == false) {
      climber1.set(0.5);
      //climber2.set(0.0);
    }*/


    /*else {
      climber1.set(0.0);
      climber2.set(0.0);
      //enabled = false;
    }*/

   /* if (logicController.getRawButton(2)){
      toggleTurret = true;
    }
    
    if (logicController.getRawButton(3)){
      toggleTurret = false;
    }

    if (toggleTurret == true) {
      turret.set(ControlMode.PercentOutput,0.10);
    }
    else if (toggleTurret == false) {
      turret.set(ControlMode.PercentOutput,0.0);
    }*/

    





    


    /*if (logicController.getRawButton(4)){
      climber2.set(0.5);
      climber1.set(0.5);
    }
    else {
      climber2.set(0.0);
      climber1.set(0.0);
    }

*/


    /*if (logicController.getRawButton(2) && y <= 20 ){
      leftFront.set(0.5);
      leftBack.set(-0.5);
      rightFront.set(0.5);
      rightBack.set(-0.5);

    }
    else if (logicController.getRawButton(2) && y >= 22 ){

      leftFront.set(-0.5);
      leftBack.set(0.5);
      rightFront.set(-0.5);
      rightBack.set(0.5);
    }
    else{
      leftFront.set(0);
      leftBack.set(0);
      rightFront.set(0);
      rightBack.set(0);
    }*/

    

    
//VERY IMPORTANT SHOOTER IS ALWAYS ON -0.65

    


    if (logicEXController.getRawAxis(3) == -1 ){
      shooter.set(ControlMode.PercentOutput, -0.45); //shooter power -0.63
    }
    else{
      shooter.set(ControlMode.PercentOutput, 0);
    }

    if (logicEXController.getRawButton(1) || logicController.getRawAxis(2) == 1){
      
      elevator.set(-0.5);
    }
    else {
      
      elevator.set(0);
      
    }


    if (logicController.getRawAxis(3)>0 || logicEXController.getRawButton(7)){
      intake1.set(-0.75);
      intake2.set(0.99);
      shaft.set(0.9);
    }
    else if (logicController.getRawButton(7) ||  logicEXController.getRawButton(3)){
      intake1.set(0.75);
      intake2.set(-0.75);
      shaft.set(-0.9);
    }

    
    else {
      intake1.set(0);
      intake2.set(0);
      shaft.set(0);
    }
    
   
    if(logicController.getRawButtonPressed(6)) {
      exampleDoublePCM.set(DoubleSolenoid.Value.kForward);
      exampleDoublePH.set(DoubleSolenoid.Value.kForward);
      //pcmCompressor.enabled();
      }
      
      else if(logicController.getRawButtonPressed(5)) {
        exampleDoublePCM.set(DoubleSolenoid.Value.kReverse);
        exampleDoublePH.set(DoubleSolenoid.Value.kReverse);
      }
      
      else {
        exampleDoublePCM.set(DoubleSolenoid.Value.kOff);
        exampleDoublePH.set(DoubleSolenoid.Value.kOff);
      }
    /*if (logicController.getRawButton(6)) (&&) (exampleDoublePCM.set(kReverse)){
      exampleDoublePCM.set(Forward);
    }
    else {
      exampleDoublePCM.set(Off);

    }*/
    
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
