<?php

  function ko() // NOK [[effortToFix=30]] {{The Cyclomatic Complexity of this function "ko" is 32 which is greater than 2 authorized.}}
//^^^^^^^^
{
  switch (foo)
  {
    case 1: // +1
    case 2: // +1
    case 3: // +1
    case 4: // +1
    case 5: // +1
    default:
    ;
  }

  if (true) { // +1
    return $a && $b || $c && $d || $e && $f || $g && $h || $i && $j || $k && $l || $m && $n || $o; // +15
  } else {
    return $a && $b || $c && $d || $e; // +5
  }

  if (true) { // +1
    return 1; // +1
  }

  while ($a) { // +1
    if (false) { // +1
      throw new Exception(); // +1
    }
  }
  return 1;
}

function ko() // NOK [[effortToFix=1]]
{
  switch (foo)
  {
    case 1: // +1
    case 2: // +1
    default:
    ;
  }
}

function ok() {
}

class C {

  public function ko() // NOK
  {
    switch (foo)
    {
      case 1: // +1
      case 2: // +1
      default:
      ;
    }
  }

  public function ok() {
  }
}

$f = function() { // NOK [[secondary=+0,+2,+3]]
//   ^^^^^^^^
  if (true) {
    return;
  }
};

function nesting() {
  $nested = function() { $a && $b; };
  if ($a) {}
  return 1;
}
