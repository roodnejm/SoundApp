<CsoundSynthesizer>
<CsOptions>
-o dac -d -b128 -B128
</CsOptions>
<CsInstruments>
nchnls=2
0dbfs=1
ksmps=64
sr = 44100

ga1 init 0

instr 1

itie tival
i_instanceNum = p4
S_xName sprintf "touch.%d.x", i_instanceNum
S_yName sprintf "touch.%d.y", i_instanceNum
chn_k "freq", 1

kx chnget S_xName
ky chnget S_yName

kenv	linsegr	1, .05, 0.5, 1, 0
asig pluck	1, 220, 220, 1, 1	

ga1 = ga1 + asig
outs ga1, ga1
endin




</CsInstruments>
<CsScore>
f1 0 16 10 1

i1 0 36000


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
