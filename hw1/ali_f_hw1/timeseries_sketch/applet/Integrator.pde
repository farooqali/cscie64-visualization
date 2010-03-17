int NUMSTEPS = 35;

class Integrator {

  float value, _start, _target;
  int _t;
  
  final int NUM_STEPS = NUMSTEPS;
  final float STEP_SIZE = 1.0 / (float)(NUM_STEPS);
  float _normalization;

  boolean _targeting;

  Integrator(float value) {
    value = value;
    _t = 0;
    _targeting = true;
    _target = value;
    
    // compute the normalization variable
    float total = 0.0;
    for ( int i = 0; i <= NUM_STEPS; i++ ) {
      total += f( (float)i*STEP_SIZE );
    }
    _normalization = 1.0/total;
  }
  
  float f( float x ) {
   return (1.0 - (2.0*x-1.0)*(2.0*x-1.0)); 
   //return 1.0;
  }

  void update() {
    if ( _targeting ) {
      value += f( (float)_t*STEP_SIZE )*_normalization*( _target - _start );
      ++_t;
      
      if ( _t > NUM_STEPS ) {
        noTarget();
      }
    }    
  }

  float value() {
    return value; }

  void target(float t) {
    _start = value;
    _t = 0;
    _targeting = true;
    _target = t;
  }


  void noTarget() {
    _targeting = false;
  }
}

class ColorIntegrator {

  color value, _start, _target;
  int _t;
  float _time;
  
  final int NUM_STEPS = NUMSTEPS;
  final float STEP_SIZE = 1.0 / (float)(NUM_STEPS);
  float _normalization;

  boolean _targeting;

  ColorIntegrator(color value) {
    value = value;
    _t = 0;
    _time = 0.0;
    
    // compute the normalization variable
    float total = 0.0;
    for ( int i = 0; i <= NUM_STEPS; i++ ) {
      total += f( (float)i*STEP_SIZE );
    }
    _normalization = 1.0/total;
  }
  
  float f( float x ) {
   return (1.0 - (2.0*x-1.0)*(2.0*x-1.0)); 
   //return 1.0;
  }

  void update() {
    if ( _targeting ) {
      //value += color(f( (float)_t*STEP_SIZE )*_normalization*( _target - _start ));
      _time += f( (float)_t*STEP_SIZE )*_normalization;
      value = lerpColor( _start, _target, _time );
      ++_t;
      
      if ( _t > NUM_STEPS ) {
        noTarget();
      }
    }    
  }

  color value() {
    return value; }

  void target(color t) {
    _start = value;
    _t = 0;
    _targeting = true;
    _target = t;
    _time = 0.0;
  }


  void noTarget() {
    _targeting = false;
  }
}
