/**
 * Convert a base64 data URL to binary
 *
 * @param  {[String]} data dataURL in the form "data:image/png;base64,****"; everything before the comma is metadata, only the part after it is needed
 * @param  {[String]} mime [description]
 * @return {[blob]}      [description]
 */
export default function(data, mime) {
  data = data.split(',')[1]
  data = window.atob(data)
  var ia = new Uint8Array(data.length)
  for (var i = 0; i < data.length; i++) {
    ia[i] = data.charCodeAt(i)
  }
  // canvas.toDataURL returns image/png by default
  return new Blob([ia], {
    type: mime
  })
}
