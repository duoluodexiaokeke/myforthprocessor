/*
 * @(#)MarkToMarkPosnSubtables.cpp	1.2 01/10/09 
 *
 * (C) Copyright IBM Corp. 1998, 1999, 2000, 2001 - All Rights Reserved
 *
 */

#include "LETypes.h"
#include "LEFontInstance.h"
#include "OpenTypeTables.h"
#include "AnchorTables.h"
#include "MarkArrays.h"
#include "GlyphPositioningTables.h"
#include "AttachmentPosnSubtables.h"
#include "MarkToMarkPosnSubtables.h"
#include "GlyphIterator.h"
#include "LESwaps.h"

LEGlyphID MarkToMarkPositioningSubtable::findMark2Glyph(GlyphIterator *glyphIterator) const
{
    if (glyphIterator->prev()) {
        return glyphIterator->getCurrGlyphID();
    }

    return 0xFFFF;
}

le_int32 MarkToMarkPositioningSubtable::process(GlyphIterator *glyphIterator, const LEFontInstance *fontInstance) const
{
    LEGlyphID markGlyph = glyphIterator->getCurrGlyphID();
    le_int32 markCoverage = getGlyphCoverage((LEGlyphID) markGlyph);

    if (markCoverage < 0) {
        // markGlyph isn't a covered mark glyph
        return 0;
    }

    LEPoint markAnchor;
    const MarkArray *markArray = (const MarkArray *) ((char *) this + SWAPW(markArrayOffset));
    le_int32 markClass = markArray->getMarkClass(markGlyph, markCoverage, fontInstance, markAnchor);
    le_uint16 mcCount = SWAPW(classCount);

    if (markClass < 0 || markClass >= mcCount) {
        // markGlyph isn't in the mark array or its
        // mark class is too big. The table is mal-formed!
        return 0;
    }

    // FIXME: we probably don't want to find a mark before a previous base glyph...
    GlyphIterator mark2Iterator(*glyphIterator, lfIgnoreLigatures /*| lfIgnoreBaseGlyphs*/);
    LEGlyphID mark2Glyph = findMark2Glyph(&mark2Iterator);
    le_int32 mark2Coverage = getBaseCoverage((LEGlyphID) mark2Glyph);
    const Mark2Array *mark2Array = (const Mark2Array *) ((char *) this + SWAPW(baseArrayOffset));
    le_uint16 mark2Count = SWAPW(mark2Array->mark2RecordCount);

    if (mark2Coverage < 0 || mark2Coverage >= mark2Count) {
        // The mark2 glyph isn't covered, or the coverage
        // index is too big. The latter means that the
        // table is mal-formed...
        return 0;
    }

    const Mark2Record *mark2Record = &mark2Array->mark2RecordArray[mark2Coverage * mcCount];
    Offset anchorTableOffset = SWAPW(mark2Record->mark2AnchorTableOffsetArray[markClass]);
    const AnchorTable *anchorTable = (const AnchorTable *) ((char *) mark2Array + anchorTableOffset);
    LEPoint mark2Anchor, markAdvance, pixels;

    anchorTable->getAnchor(mark2Glyph, fontInstance, mark2Anchor);

    fontInstance->getGlyphAdvance(markGlyph, pixels);
    fontInstance->pixelsToUnits(pixels, markAdvance);

    float anchorDiffX = mark2Anchor.fX - markAnchor.fX;
    float anchorDiffY = mark2Anchor.fY - markAnchor.fY;

    if (glyphIterator->isRightToLeft()) {
        float adjustX = markAdvance.fX + anchorDiffX;

        glyphIterator->adjustCurrGlyphPositionAdjustment(anchorDiffX, -anchorDiffY, -adjustX, anchorDiffY);
    } else {
        LEPoint mark2Advance;

        fontInstance->getGlyphAdvance(mark2Glyph, pixels);
        fontInstance->pixelsToUnits(pixels, mark2Advance);

        float adjustX = mark2Advance.fX - anchorDiffX;
        float advAdjustX = adjustX - markAdvance.fX;

        glyphIterator->adjustCurrGlyphPositionAdjustment(-adjustX, -anchorDiffY, advAdjustX, anchorDiffY);
    }

    return 1;
}
