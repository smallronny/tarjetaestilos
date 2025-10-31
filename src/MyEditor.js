import ClassicEditorBase from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';
import Essentials from '@ckeditor/ckeditor5-essentials/src/essentials';
import Paragraph from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import Bold from '@ckeditor/ckeditor5-basic-styles/src/bold';
import Italic from '@ckeditor/ckeditor5-basic-styles/src/italic';
import Heading from '@ckeditor/ckeditor5-heading/src/heading';
import Link from '@ckeditor/ckeditor5-link/src/link';
import List from '@ckeditor/ckeditor5-list/src/list';
import Table from '@ckeditor/ckeditor5-table/src/table';
import BlockQuote from '@ckeditor/ckeditor5-block-quote/src/blockquote';
import Undo from '@ckeditor/ckeditor5-undo/src/undo';
import SourceEditing from '@ckeditor/ckeditor5-source-editing/src/sourceediting';

// Adapter personalizado


class MyEditor extends ClassicEditorBase {}

MyEditor.builtinPlugins = [
  Essentials,
  Paragraph,
  Bold,
  Italic,
  Heading,
  Link,
  List,
  Table,
  BlockQuote,
  Undo,
  SourceEditing
];

MyEditor.defaultConfig = {
  toolbar: [
    'heading', '|', 'bold', 'italic', 'link', 'bulletedList', 'numberedList',
    '|', 'blockQuote', 'insertTable', 'undo', 'redo', '|', 'sourceEditing'
  ],
};

export default MyEditor;