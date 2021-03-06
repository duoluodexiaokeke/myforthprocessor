/*
 * @(#)GlyphPositioningTables.cpp	1.10 01/10/09 
 *
 * (C) Copyright IBM Corp. 1998, 1999, 2000, 2001 - All Rights Reserved
 *
 */

#include "LETypes.h"
#include "LEFontInstance.h"
#include "OpenTypeTables.h"
#include "Lookups.h"
#include "GlyphDefinitionTables.h"
#include "GlyphPositionAdjustments.h"
#include "GlyphPositioningTables.h"
#include "GlyphPosnLookupProc.h"

void GlyphPositioningTableHeader::process(LEGlyphID *glyphs, GlyphPositionAdjustment *glyphPositionAdjustments,
                                          const LETag **glyphTags, le_int32 glyphCount, le_bool rightToLeft,
                                          LETag scriptTag, LETag languageTag,
                                          const GlyphDefinitionTableHeader *glyphDefinitionTableHeader,
                                          const LEFontInstance *fontInstance) const
{
    GlyphPositioningLookupProcessor processor(this, scriptTag, languageTag);

    processor.process(glyphs, glyphPositionAdjustments, glyphTags, glyphCount, rightToLeft,
        glyphDefinitionTableHeader, fontInstance);
}

