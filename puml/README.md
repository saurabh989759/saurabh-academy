# UML Diagrams and ERD - Usage Guide

This directory contains PlantUML diagram files for the Academy Backend project.

## Files

1. **`complete-class-diagram.puml`** - Comprehensive UML class diagram showing all layers
2. **`enhanced-erd.puml`** - Detailed Entity Relationship Diagram (ERD) with all table details
3. **`class-diagram.puml`** - Simplified class diagram (entity relationships only)
4. **`erd.puml`** - Simplified ERD (basic table structure)

## How to View Diagrams

### Option 1: PlantUML Online Viewer (Quick View)

1. Go to http://www.plantuml.com/plantuml/uml/
2. Copy the contents of any `.puml` file
3. Paste into the editor
4. View the rendered diagram
5. Export as PNG, SVG, or PDF

### Option 2: VS Code Extension

1. Install "PlantUML" extension in VS Code
2. Open any `.puml` file
3. Press `Alt+D` (Windows/Linux) or `Option+D` (Mac) to preview
4. Right-click to export as image

### Option 3: IntelliJ IDEA Plugin

1. Install "PlantUML integration" plugin
2. Open any `.puml` file
3. Right-click → "PlantUML" → "Preview Diagram"
4. Export as image from preview window

### Option 4: Draw.io (Recommended for Editing)

#### Method A: Direct Import (if supported)

1. Open https://app.diagrams.net/ (Draw.io)
2. File → Import → PlantUML
3. Select the `.puml` file
4. Draw.io will convert and display the diagram

#### Method B: Via PlantUML Server

1. **Generate Image First**:
   - Use PlantUML online viewer or VS Code extension
   - Export diagram as PNG or SVG

2. **Import to Draw.io**:
   - Open Draw.io
   - File → Import → Image
   - Select the exported PNG/SVG
   - The diagram will be imported as an image

3. **Edit in Draw.io**:
   - You can add shapes, annotations, and modify layout
   - Change colors and styling
   - Add additional details or notes

#### Method C: Manual Recreation (Best for Customization)

1. Open Draw.io
2. Create a new diagram
3. Use the PlantUML diagram as reference
4. Recreate using Draw.io shapes and connectors
5. This gives you full control over styling and layout

## Diagram Details

### Complete Class Diagram (`complete-class-diagram.puml`)

**Shows:**
- Entity layer (domain models)
- Repository layer (data access)
- Service layer (business logic)
- Controller layer (REST API)
- Kafka layer (event-driven)
- Aspect layer (AOP)
- All relationships and dependencies

**Use Cases:**
- Understanding system architecture
- Onboarding new developers
- Documentation and presentations
- Design reviews

### Enhanced ERD (`enhanced-erd.puml`)

**Shows:**
- All 9 database tables
- Complete field definitions with data types
- Primary keys and foreign keys
- Indexes and constraints
- Relationship cardinalities
- Optimistic locking columns
- Notes explaining features

**Use Cases:**
- Database design documentation
- Schema reviews
- Understanding relationships
- Migration planning

## Exporting for Documentation

### For Markdown/PDF Reports

1. **Export as PNG** (for reports):
   - High resolution (300 DPI recommended)
   - Use PlantUML online viewer or VS Code
   - File → Export → PNG

2. **Export as SVG** (for web/docs):
   - Scalable vector format
   - Better quality for zooming
   - Use PlantUML online viewer

3. **Export as PDF** (for presentations):
   - Professional format
   - Use PlantUML online viewer
   - File → Export → PDF

### Recommended Export Settings

- **PNG**: 300 DPI, transparent background (optional)
- **SVG**: Default settings (scalable)
- **PDF**: A4 or Letter size

## Tips for Draw.io

### Styling Tips

1. **Color Coding**:
   - Use different colors for different layers
   - Entities: Light blue
   - Services: Light green
   - Controllers: Light yellow
   - Repositories: Light gray

2. **Layout**:
   - Group related components
   - Use alignment tools
   - Maintain consistent spacing

3. **Annotations**:
   - Add notes for complex relationships
   - Include cardinality labels
   - Add legends for symbols

### Keyboard Shortcuts

- `Ctrl+G` / `Cmd+G`: Group selected elements
- `Ctrl+Shift+G` / `Cmd+Shift+G`: Ungroup
- `Ctrl+D` / `Cmd+D`: Duplicate
- `Ctrl+Shift+Arrow`: Nudge elements
- `Ctrl+L` / `Cmd+L`: Lock/unlock elements

## Troubleshooting

### PlantUML Not Rendering

1. Check syntax errors in `.puml` file
2. Ensure PlantUML server is accessible
3. Try different PlantUML viewer

### Draw.io Import Issues

1. If direct PlantUML import fails, use Method B (via image)
2. Ensure file encoding is UTF-8
3. Check file path doesn't contain special characters

### Diagram Too Large

1. Use PlantUML's `scale` directive: `!define SCALE 0.75`
2. Split into multiple diagrams
3. Use `hide` directive to hide less important elements

## Updating Diagrams

When updating diagrams:

1. **Edit the `.puml` file** (source of truth)
2. **Regenerate images** using PlantUML
3. **Update Draw.io** if using manual recreation
4. **Update documentation** references

## Version Control

- `.puml` files are text-based and version-control friendly
- Commit both `.puml` files and exported images
- Use `.gitignore` for temporary export files if needed

## Additional Resources

- PlantUML Documentation: https://plantuml.com/
- Draw.io Documentation: https://www.diagrams.net/doc/
- PlantUML Syntax Reference: https://plantuml.com/guide

