Graphics3D 1024,768,32,1
SetBuffer BackBuffer()
AmbientLight 0,0,0

;peripheral lights
light=CreateLight(3) 
sunspot=CreateLight(3)
LightConeAngles sunspot,0,15
PositionEntity sunspot,0,500,0
;declare
red=150
blue=250
green=150

;create camera
camera=CreateCamera()
CameraViewport camera,0,0,1024,768
PositionEntity camera,0,5,1.5
CameraClsColor camera,150,150,255
CameraRange camera,.1,100000

;secondary camera

;collisions
Collisions 2,3,2,1
Collisions 1,2,2,1
Collisions 2,2,2,2

;;;loadsound;;;;
lazersound=LoadSound("laser.wav")


;create ground
ground=CreatePlane()
groundtexture=LoadTexture("grass1.jpg")
ScaleTexture groundtexture,5,5
EntityTexture ground,groundtexture
;create tower
tower= CreateCylinder(32)
ScaleEntity tower,1,3,1
PositionEntity tower,0,1.5,0
towertexture=LoadTexture("wood.jpg")
ScaleTexture towertexture,.2,.2
EntityTexture tower,towertexture
;turret part
turret = CreateSphere(16)
turrettexture=LoadTexture("turret.jpg")
ScaleTexture turrettexture,1,2
PositionTexture turrettexture,.5,1
EntityTexture turret,turrettexture
EntityShininess turret,.5
PositionEntity turret,0,5,0

;cannon part
cannon=CreateCylinder(16)
cannontexture=LoadTexture("cannon.bmp")
EntityTexture cannon,cannontexture
PositionEntity cannon,0,5,3
ScaleEntity cannon,.2,2,.2
EntityShininess cannon,1
RotateEntity cannon,90,0,0
EntityParent cannon,turret 
EntityParent camera,cannon



;createsun
sun=CreateSphere(32)
ScaleEntity sun,500,500,500
suntex=LoadTexture("sun.png")


;mini towers
Type minitower
Field power
Field hp
Field armor
Field speed
End Type 

;Lazerbeam 
Type lazerbeam
Field speed
Field power
Field exist
Field distance
Field light
End Type 

;enemy balls
Type deathball
Field size#
Field hp
Field exist
Field id
End Type 

speed=1
power=1
armor=1
hp=1

;towerlight
towerlight=CreateLight(2)
PositionEntity towerlight,0,10,3
RotateEntity towerlight,90,0,0 
EntityParent towerlight,cannon
LightRange towerlight,2
EntityParent towerlight,cannon



timer=CreateTimer(60)

 enemies=10
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;Main loop::::::::::::::::::::;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  While Not KeyHit(1)


;gamelighting
PointEntity sunspot,sun
PointEntity light,tower
sunmove#=sunmove#+.1
If sunmove#=360 Then sunmove#=0
sunx#=10000*Cos(sunmove#)
suny#=10000*Sin(sunmove#)
PositionEntity sun,0,sunx#,suny#
PositionEntity light,0,sunx#,suny#
sunlight=sunx#/4


If sunlight>0 And sunlight<630 Then blue=(sunlight/5)*2
If sunlight>-130 And sunlight<630 Then green=sunlight/5+25
If sunlight>-250 And sunlight<505 Then red=sunlight/5+50
LightColor light,red,green,blue
LightColor sunspot,150,green,0
AmbientLight red,green,blue

CameraClsColor camera,red,green,blue
If sunlight<-250 Then LightRange sunspot,0 Else LightRange sunspot,10000
If sunlight<-250 Then LightRange light,0 Else LightRange light,10000





;move cannon
 If MouseX()<10 Or MouseX()>1000 Then
MoveMouse 500,250
EndIf
 If MouseY()<10 Or MouseY()>500 Then
MoveMouse 500,250
EndIf 
mxs#=mxs#-MouseXSpeed()*.1
mys#=mys#+MouseYSpeed()*.1
If mxs#<1 Then mxs#=360
If mxs#>360 Then mxs=1
If mys#>30 Then mys#=30
If mys#<-89 Then mys#=-89
RotateEntity turret,mys#,mxs#,0
If KeyDown(17) Then MoveEntity camera,0,0,-1

;; down to business

;fire cannon/create laser beam
If MouseHit(1) Then
ab.lazerbeam=New lazerbeam
ab\power=power
ab\speed=speed
ab\exist=CreateSphere(16)
EntityType ab\exist,1
PositionEntity ab\exist,0,5,0
ScaleEntity ab\exist,.1,.1,10
PlaySound lazersound
EntityColor ab\exist,power,255,255
RotateEntity ab\exist,mys#,mxs#,0
EndIf
 



;delete deathballs after lazer hits
For check.deathball=Each deathball
PointEntity check\exist,tower
MoveEntity check\exist,0,0,check\size#/2
If CountCollisions(check\exist) Then
check\size#=check\size#-1
If check\size#>0 Then MoveEntity check\exist,0,-1,0
ScaleEntity check\exist,check\size#,check\size#,check\size#
If check\size#=0 Then
FreeEntity check\exist
Delete check
EndIf 
EndIf

Next

;move lazerbeam 
.lazerbeams
For ab.lazerbeam=Each lazerbeam
MoveEntity ab\exist,0,0,ab\speed
ab\distance=ab\distance+ab\speed   
If ab\distance>1000 Then 
FreeEntity ab\exist
Delete ab
Else 
If CountCollisions(ab\exist) Then FreeEntity ab\exist Delete ab
EndIf
Next 




;level setup
If enemies>0 Then 
For ballscreate=1 To enemies
newball.deathball=New deathball
size=Rand(1,3)
newball\exist= CreateSphere(16)
ScaleEntity newball\exist,size,size,size
EntityType newball\exist,2 
newball\size= size
degrees#=Rnd(.01,360)
PositionEntity newball\exist,1000*Cos(degrees#),size,1000*Sin(degrees#)
enemies=enemies-1
Next
EndIf 


UpdateWorld
RenderWorld




Plot GraphicsWidth()/2,GraphicsHeight()/2

Text 10,10, sunx#
Text 10,20, resources
VWait 
Flip
  Wend
  End