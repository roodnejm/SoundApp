<CsoundSynthesizer>
<CsOptions>
-o dac -d -B512 -b256
</CsOptions>
<CsInstruments>
nchnls=1
0dbfs=1
ksmps=64
; Set the audio sample rate to 44100 Hzs
sr = 44100

instr 1

isl chnget "slider" 
;input
ksl chnget "slider" 
ksl port ksl, 0.01, isl 
a2 expsegr 0.001,0.01,p4,p3-0.01, 0.001, 0.1, 0.001
a1 oscili a2, p5*(1+ksl), 1
out a1

endin

</CsInstruments>
<CsScore>
f1 0 16384 10 1
;{ 10 CNT
;i1 [$CNT*0.2] 0.5 0.5 [440*(2^[$CNT/12])]
;}

i1 0 1 0.5 440
</CsScore>
</CsoundSynthesizer>

<bsbPanel>
 <label>Widgets</label>
 <objectName/>
 <x>100</x>
 <y>100</y>
 <width>320</width>
 <height>240</height>
 <visible>true</visible>
 <uuid/>
 <bgcolor mode="nobackground">
  <r>255</r>
  <g>255</g>
  <b>255</b>
 </bgcolor>
</bsbPanel>
<bsbPresets>
</bsbPresets>
