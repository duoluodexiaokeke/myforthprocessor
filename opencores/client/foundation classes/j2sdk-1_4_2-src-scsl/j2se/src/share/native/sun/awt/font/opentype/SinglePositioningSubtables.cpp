/*
 * @(#)SinglePositioningSubtables.cpp	1.10 01/10/09 
 *
 * (C) Copyright IBM Corp. 1998, 1999, 2000, 2001 - All Rights Reserved
 *
 */

#include "LETypes.h"
#include "LEFontInstance.h"
#include "OpenTypeTables.h"
#include "GlyphPositioningTables.h"
#include "SinglePositioningSubtables.h"
#include "ValueRecords.h"
#include "GlyphIterator.h"
#include "GlyphPositionAdjustments.h"
#include "LESwaps.h"

le_uint32 SinglePositioningSubtable::process(GlyphIterator *glyphIterator, const LEFontInstance *fontInstance) const
{
    switch(SWAPW(subtableFormat))
    {
    case 0:
        return 0;

    case 1:
    {
        const SinglePositioningFormat1Subtable *subtable = (const SinglePositioningFormat1Subtable *) this;

        return subtable->process(glyphIterator, fontInstance);
    }

    case 2:
    {
        const SinglePositioningFormat2Subtable *subtable = (const SinglePositioningFormat2Subtable *) this;

        return subtable->process(glyphIterator, fontInstance);
    }

    default:
        return 0;
    }
}

le_uint32 SinglePositioningFormat1Subtable::process(GlyphIterator *glyphIterator, const LEFontInstance *fontInstance) const
{
    LEGlyphID glyph = (LEGlyphID) glyphIterator->getCurrGlyphID();
    le_int32 coverageIndex = getGlyphCoverage(glyph);

    if (coverageIndex >= 0) {
        GlyphPositionAdjustment adjustment;

        glyphIterator->getCurrGlyphPositionAdjustment(adjustment);

        valueRecord.adjustPosition(SWAPW(valueFormat), (const char *) this, adjustment, fontInstance);

        glyphIterator->setCurrGlyphPositionAdjustment(&adjustment);

        return 1;
    }

    return 0;
}

le_uint32 SinglePositioningFormat2Subtable::process(GlyphIterator *glyphIterator, const LEFontInstance *fontInstance) const
{
    LEGlyphID glyph = (LEGlyphID) glyphIterator->getCurrGlyphID();
    le_int16 coverageIndex = (le_int16) getGlyphCoverage(glyph);

    if (coverageIndex >= 0) {
        GlyphPositionAdjustment adjustment;

        glyphIterator->getCurrGlyphPositionAdjustment(adjustment);

        valueRecordArray[0].adjustPosition(coverageIndex, SWAPW(valueFormat), (const char *) this, adjustment, fontInstance);

        glyphIterator->setCurrGlyphPositionAdjustment(&adjustment);

        return 1;
    }

    return 0;
}

