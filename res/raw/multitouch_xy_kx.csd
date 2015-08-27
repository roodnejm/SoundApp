<CsoundSynthesizer>
<CsOptions>
-o dac -d -b128 -B128
</CsOptions>
<CsInstruments>
nchnls=2
0dbfs=1
ksmps=1
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


kenv linsegr 0, .0001, 1, .1, 1, .25, 0
a1 vco2 .5 * kenv, 1 *kx*ky , 0

ga1 = ga1 + a1

endin

instr 2

;kcutoff chnget "cutoff"
;kresonance chnget "resonance"

kcutoff = 6000
kresonance = .2

k1 chnget "freq"
printk2 k1

a1 moogladder ga1, kcutoff, kresonance

aL, aR reverbsc a1, a1, .72, 5000

outs aL, aR

ga1 = 0

endin

  instr 3 ; write to a file (always on in order to record everything)
aSig    monitor                              ; read audio from output bus
        fout     "/mnt/sdcard/record01.wav",4,aSig   ; write audio to file (16bit mono)
  endin


</CsInstruments>
<CsScore>
f1 0 16 10 1

i2 0 360000

; activate recording instrument to encapsulate the entire performance
i 3 0 4
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
