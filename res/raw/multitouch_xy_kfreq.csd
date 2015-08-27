<CsoundSynthesizer>
<CsOptions>
-o dac -d -b4096 -B4096
</CsOptions>
<CsInstruments>
nchnls=2
0dbfs=1
ksmps=64
sr = 44100

ga1 init 0
chn_k "freq", 1

instr 1

itie tival
i_instanceNum = p4
S_xName sprintf "touch.%d.x", i_instanceNum
S_yName sprintf "touch.%d.y", i_instanceNum


kx chnget S_xName
ky chnget S_yName 

kenv linsegr 0, .0001, 1, .1, 1, .25, 0
a1 vco2 .5, 1*kx*ky , 0

ga1 = ga1 + a1

endin

instr 2

;kcutoff chnget "cutoff"
;kresonance chnget "resonance"

kcutoff = 1000
kresonance = .2

isl chnget "freq" 
ksl chnget "freq"

printk2 ksl
ksl port ksl, 0.01, isl 

ihertz chnget "hertz" 
khertz chnget "hertz"
printk2 khertz
khertz port khertz, 0.01, ihertz 

a1 moogladder ga1, kcutoff*khertz, kresonance



aL reverb a1, ksl



outs aL, aL

ga1 = 0

endin


</CsInstruments>
<CsScore>
f1 0 16 10 1

i2 0 360000


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
