package me.dmdev.rxpm.map.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RestoreViewOnCreateController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import io.reactivex.disposables.CompositeDisposable
import me.dmdev.rxpm.PresentationModel
import me.dmdev.rxpm.map.MapPmExtension
import me.dmdev.rxpm.map.MapPmView
import me.dmdev.rxpm.map.delegate.MapPmControllerDelegate

/**
 * Predefined [Conductor's Controller][RestoreViewOnCreateController] implementing the [MapPmView].
 *
 * Just override the [providePresentationModel], [onBindPresentationModel]
 * and [onBindMapPresentationModel] methods and you are good to go.
 *
 * You also need to call the [onLowMemory] method yourself,
 * because the base [controller][Controller] does not have corresponding callback.
 * See https://github.com/bluelinelabs/Conductor/issues/59
 *
 * If extending is not possible you can implement [MapPmView],
 * create a [MapPmControllerDelegate] and pass the lifecycle callbacks to it.
 * See this class's source code for the example.
 */
abstract class MapPmController<PM>(args: Bundle? = null) : RestoreViewOnCreateController(args),
                                                           MapPmView<PM>
where PM : PresentationModel, PM : MapPmExtension {

    private val delegate by lazy(LazyThreadSafetyMode.NONE) { MapPmControllerDelegate(this) }

    final override val compositeUnbind = CompositeDisposable()

    final override val presentationModel get() = delegate.presentationModel

    final override var mapView: MapView? = null
    final override var googleMap: GoogleMap? = null

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = createView(inflater, container, savedViewState)
        delegate.onCreateView(view, savedViewState)
        return view
    }

    /**
     * Replaces the [onCreateView] that the library hides for internal use.
     */
    abstract fun createView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View

    override fun onAttach(view: View) {
        super.onAttach(view)
        delegate.onAttach()
    }

    override fun onDetach(view: View) {
        super.onDetach(view)
        delegate.onDetach()
    }

    override fun onSaveViewState(view: View, outState: Bundle) {
        super.onSaveViewState(view, outState)
        delegate.onSaveViewState(outState)
    }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)
        delegate.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    /**
     * [MapView] wants this to be called.
     * You need to call this method yourself because
     * the base [controller][Controller] does not have corresponding callback.
     * See https://github.com/bluelinelabs/Conductor/issues/59
     */
    fun onLowMemory() {
        delegate.onLowMemory()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        delegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}