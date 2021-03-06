/*
 * @(#)SegmentArrayProcessor.cpp	1.8 01/10/09 
 *
 * (C) Copyright IBM Corp. 1998, 1999, 2000, 2001 - All Rights Reserved
 *
 */

#include "LETypes.h"
#include "MorphTables.h"
#include "SubtableProcessor.h"
#include "NonContextualGlyphSubst.h"
#include "NonContextualGlyphSubstProc.h"
#include "SegmentArrayProcessor.h"
#include "LESwaps.h"

SegmentArrayProcessor::SegmentArrayProcessor()
{
}

SegmentArrayProcessor::SegmentArrayProcessor(const MorphSubtableHeader *morphSubtableHeader)
  : NonContextualGlyphSubstitutionProcessor(morphSubtableHeader)
{
    const NonContextualGlyphSubstitutionHeader *header = (const NonContextualGlyphSubstitutionHeader *) morphSubtableHeader;

    segmentArrayLookupTable = (const SegmentArrayLookupTable *) &header->table;
}

SegmentArrayProcessor::~SegmentArrayProcessor()
{
}

void SegmentArrayProcessor::process(LEGlyphID *glyphs, le_int32 *charIndices, le_int32 glyphCount)
{
    const LookupSegment *segments = segmentArrayLookupTable->segments;
    le_int32 glyph;

    for (glyph = 0; glyph < glyphCount; glyph += 1) {
        const LookupSegment *lookupSegment = segmentArrayLookupTable->lookupSegment(segments, glyphs[glyph]);

        if (lookupSegment != NULL)  {
            le_int16 firstGlyph = SWAPW(lookupSegment->firstGlyph);
            le_int16 offset = SWAPW(lookupSegment->value);

            if (offset != 0) {
                le_int16 *glyphArray = (le_int16 *) ((char *) subtableHeader + offset);
                le_int16 newGlyph = glyphArray[glyphs[glyph] - firstGlyph];
                
                glyphs[glyph] = SWAPW(newGlyph);
            } 
        }
    }
}
 
