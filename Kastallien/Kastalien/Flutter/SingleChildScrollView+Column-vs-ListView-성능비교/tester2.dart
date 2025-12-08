// scroll_bench_20s.dart
import 'dart:convert';
import 'dart:ui';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';

/// --- 최소 FrameTiming 수집기 ---
class _FrameTimingRecorder {
  _FrameTimingRecorder(this.label);
  final String label;
  bool _recording = false;
  final List<FrameTiming> _frames = [];

  void _on(List<FrameTiming> ts) { if (_recording) _frames.addAll(ts); }

  void start() {
    if (_recording) return;
    SchedulerBinding.instance.addTimingsCallback(_on);
    _recording = true;
  }

  Map<String, dynamic> stopAndSummarize() {
    if (!_recording) return {'error': 'not_recording'};
    _recording = false;
    SchedulerBinding.instance.removeTimingsCallback(_on);

    final hz = _detectHz() ?? 60.0;
    final budget = 1000.0 / hz;
    double ms(Duration d) => d.inMicroseconds / 1000.0;

    final build = <double>[], raster = <double>[], worst = <double>[];
    for (final f in _frames) {
      final b = ms(f.buildDuration), r = ms(f.rasterDuration);
      build.add(b); raster.add(r); worst.add(b > r ? b : r);
    }

    Map<String, dynamic> stats(List<double> xs) {
      if (xs.isEmpty) return {'count':0,'avg_ms':0,'p95_ms':0,'p99_ms':0,'max_ms':0,'min_ms':0};
      xs.sort();
      double q(double p){final pos=(xs.length-1)*p;final lo=pos.floor(),hi=pos.ceil(); if(lo==hi)return xs[lo]; final t=pos-lo; return xs[lo]*(1-t)+xs[hi]*t;}
      double avg = xs.reduce((a,b)=>a+b)/xs.length;
      double r(double v)=>double.parse(v.toStringAsFixed(3));
      return {'count':xs.length,'avg_ms':r(avg),'p95_ms':r(q(0.95)),'p99_ms':r(q(0.99)),'max_ms':r(xs.last),'min_ms':r(xs.first)};
    }

    final missed = worst.where((w)=>w>budget).length;
    final total = worst.length;
    double r(double v)=>double.parse(v.toStringAsFixed(3));

    return {
      'label': label,
      'device_refresh_hz': r(hz),
      'frame_budget_ms': r(budget),
      'frames_captured': total,
      'missed_frames': missed,
      'missed_pct': total==0?0.0:r(missed*100.0/total),
      'build': stats(build),
      'raster': stats(raster),
      'worst_phase': stats(worst),
    };
  }

  static double? _detectHz() {
    try {
      final views = PlatformDispatcher.instance.views;
      if (views.isNotEmpty) {
        final hz = views.first.display.refreshRate;
        if (hz != null && hz > 0) return hz;
      }
    } catch (_) {}
    return null;
  }
}

/// --- 버튼 클릭 → 버튼 숨김 → (다음 프레임부터) 20초간 측정+왕복 스크롤 → 로그 ---
class ScrollBench20s extends StatefulWidget {
  const ScrollBench20s({
    super.key,
    required this.childBuilder,   // 기존 ListView를 이 빌더에서 반환, 제공한 controller 사용
    required this.label,
    this.duration = const Duration(seconds: 20),
    this.pixelsPerSecond = 2000.0, // 스크롤 속도(px/s)
  });

  final Widget Function(BuildContext context, ScrollController controller) childBuilder;
  final String label;
  final Duration duration;
  final double pixelsPerSecond;

  @override
  State<ScrollBench20s> createState() => _ScrollBench20sState();
}

class _ScrollBench20sState extends State<ScrollBench20s> {
  final _controller = ScrollController();
  late final _FrameTimingRecorder _rec = _FrameTimingRecorder(widget.label);
  bool _started = false;
  bool _running = false;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  Duration _durationFor(double from, double to) {
    final dist = (to - from).abs();
    final seconds = dist / widget.pixelsPerSecond;
    final ms = (seconds * 1000).clamp(1, 600000).round();
    return Duration(milliseconds: ms);
  }

  Future<void> _loopScroll() async {
    while (_running && _controller.hasClients) {
      final pos = _controller.position;
      final max = pos.maxScrollExtent;
      final min = pos.minScrollExtent;
      if (max <= min) {
        // 스크롤 범위가 없으면 의미 없는 벤치 → 기록만 하고 종료
        // ignore: avoid_print
        print('⚠️ ScrollBench20s: maxScrollExtent == minScrollExtent (콘텐츠가 한 화면 이내)');
        break;
      }

      // ↓ 바닥까지
      final d1 = _durationFor(pos.pixels, max);
      await _controller.animateTo(max, duration: d1, curve: Curves.linear);
      if (!_running) break;

      // ↑ 천장까지
      final d2 = _durationFor(_controller.position.pixels, min);
      await _controller.animateTo(min, duration: d2, curve: Curves.linear);
      if (!_running) break;
    }
  }

  Future<void> _start() async {
    if (_started) return;
    setState(() => _started = true); // 버튼 숨김 (측정 전)
    // 다음 프레임부터 측정 시작 + 스크롤 시작
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      _running = true;
      _rec.start();
      // 20초 타이머
      Future.delayed(widget.duration, () {
        if (!_running) return;
        _running = false;
        _controller.stop();
        final summary = _rec.stopAndSummarize();
        // ignore: avoid_print
        print('📊 ScrollBench20s[${widget.label}] ${jsonEncode(summary)}');
      });
      // 스크롤 루프
      await _loopScroll();
    });
  }

  @override
  Widget build(BuildContext context) {
    if (!_started) {
      return Center(
        child: TextButton(onPressed: _start, child: const Text('20초 벤치 시작')),
      );
    }
    // 버튼은 사라지고, 기존 리스트뷰만 표시됨
    return widget.childBuilder(context, _controller);
  }
}
