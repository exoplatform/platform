/*
* imgscale, Automatically scales images to fit or fill their parent container.
* Note: The defined parent container of the image must have a defined height and width in CSS.
* By: Kelly Meath
* Website : http://imgscale.kjmeath.com
* Version: 1.0.0
* Updated: March 22nd, 2011
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
(function($) {
    $.fn.imgscale = function(params) {

        params = $.extend( {parent: false, scale: 'fill', center: true, fade: 0}, params);
        
        var _parentHeight, _parentWidth, _imgHeight, _imgWidth, _imgNewWidth, _imgNewHeight, _marginLeft, _marginTop;
        
        this.each( function() {
            var $img = $(this);
            var $parent = ( !params.parent ? $img.parent() : $img.parents( params.parent ) );
            $parent.css({opacity: 0, overflow: 'hidden'});
            if( $parent.length > 0 ) {
                $img.removeAttr('height').removeAttr('width');
                if (this.complete) {
                    _scaleImage( $img, $parent, false );
                } else {
                    $img.load( function() {
                        _scaleImage( $img, $parent, true );
                    });
                }
            }
        });
        function _scaleImage( $img, $parent, _loadedImg ) {

            _parentHeight = $parent.height();
            _parentWidth = $parent.width();
            _imgHeight = $img.height();
            _imgWidth = $img.width();

            _getParentShape();

            function _getParentShape() {
                if( _parentWidth > _parentHeight )
                    _getImageShape( 'w' ); // wide parent
                else if( _parentWidth < _parentHeight )
                    _getImageShape( 't' ); // tall parent
                else if( _parentWidth == _parentHeight )
                    _getImageShape( 's' ); // square parent
            }

            function _getImageShape( _parentShape ) {
                if( _imgWidth > _imgHeight )
                    _compareShapes( _parentShape, 'w' ) // wide image
                else if( _imgWidth < _imgHeight )
                    _compareShapes( _parentShape, 't' ) // tall image
                else if( _imgWidth == _imgHeight )
                    _compareShapes( _parentShape, 's' ) // sqaure image
            }

            function _compareShapes( _parentShape, _imgShape ) {
                if( _parentShape == 'w' && _imgShape == 'w' )
                    _calulateScale();
                else if( _parentShape == 'w' && _imgShape == 't' )
                    _reiszeImage( 'w' );
                else if( _parentShape == 'w' && _imgShape == 's' )
                    _reiszeImage( 'w' );
                else if( _parentShape == 't' && _imgShape == 'w' )
                    _reiszeImage( 'w' );
                else if( _parentShape == 't' && _imgShape == 't' )
                    _calulateScale();
                else if( _parentShape == 't' && _imgShape == 's' )
                    _reiszeImage( 't' );
                else if( _parentShape == 's' && _imgShape == 'w' )
                    _reiszeImage( 't' );
                else if( _parentShape == 's' && _imgShape == 't' )
                    _reiszeImage( 'w' );
                else if( _parentShape == 's' && _imgShape == 's' )
                    _reiszeImage( 'w' );
            }

            function _calulateScale() {
                if( (_imgWidth * _parentHeight / _imgWidth ) >= _parentWidth )
                    _reiszeImage( 't' );
                else
                    _reiszeImage( 'w' );
            }

            function _reiszeImage( _scale ) {
                switch( _scale ) {
                    case 't':
                      if( params.scale == 'fit' )
                        $img.attr( 'width', _parentWidth );
                      else
                        $img.attr( 'height', _parentHeight );
                        break;
                    case 'w':
                        if( params.scale == 'fit' )
                          $img.attr( 'height', _parentHeight );
                        else
                          $img.attr( 'width', _parentWidth );
                        break;
                }
                if( params.center )
                  _repositionImage();
                else
                  _showImage();
            }

            function _repositionImage() {
                _imgNewWidth = $img.width();
                _imgNewHeight = $img.height();

                if( _imgNewHeight > _parentHeight ) {
                    _marginTop = '-' + ( Math.floor( ( _imgNewHeight - _parentHeight ) / 2 ) ) + 'px';
                    $img.css( 'margin-top', _marginTop );
                }

                if( _imgNewWidth > _parentWidth ) {
                    _marginLeft = '-' + ( Math.floor( ( _imgNewWidth - _parentWidth ) / 2 ) ) + 'px';
                    $img.css( 'margin-left', _marginLeft );
                }
                _showImage();
            }
            
            function _showImage(){
              if( params.fade > 0 && _loadedImg )
                $parent.animate({opacity : 1}, params.fade);
              else
                $parent.css('opacity', 1);
            }
        }

    };
})(jQuery);